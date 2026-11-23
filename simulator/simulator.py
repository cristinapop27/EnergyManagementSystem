import argparse
import json
import random
import time
from datetime import datetime, timedelta
from pathlib import Path

import pika

QUEUE = "measurements-queue"
STEP = timedelta(minutes=10)

# (from hour, to hour, min kWh, max kWh) per 10 minutes
HOUR_RANGES = [
    (0, 6, 0.05, 0.10),     # night
    (6, 9, 0.40, 0.80),     # morning
    (9, 17, 0.05, 0.15),    # away
    (17, 22, 0.50, 1.00),   # evening
    (22, 24, 0.15, 0.30),   # late evening
]

ENV_FILE = Path(__file__).resolve().parent.parent / ".env"
env = dict(line.strip().split("=", 1) for line in ENV_FILE.read_text().splitlines() if "=" in line)


def consumption(moment):
    for start, end, low, high in HOUR_RANGES:
        if start <= moment.hour < end:
            return round(random.uniform(low, high), 3)


parser = argparse.ArgumentParser()
parser.add_argument("--device-id", required=True)
parser.add_argument("--start", help="simulated start, e.g. 2026-11-17T00:00 (default: now)")
parser.add_argument("--interval", type=float, default=600, help="real seconds between messages")
args = parser.parse_args()

moment = datetime.fromisoformat(args.start) if args.start else datetime.now().replace(second=0, microsecond=0)

credentials = pika.PlainCredentials(env["RABBITMQ_USER"], env["RABBITMQ_PASSWORD"])

with pika.Connection(pika.ConnectionParameters("localhost", credentials=credentials)) as connection:
    channel = connection.channel()
    channel.queue_declare(queue=QUEUE, durable=True)

    try:
        while True:
            message = {
                "timestamp": int(moment.timestamp() * 1000),
                "device_id": args.device_id,
                "measurement_value": consumption(moment),
            }
            channel.basic_publish(exchange="", routing_key=QUEUE, body=json.dumps(message),
                                  properties=pika.BasicProperties(content_type="application/json"))
            print(f"{moment:%Y-%m-%d %H:%M}  sent {message}")
            moment += STEP
            time.sleep(args.interval)
    except KeyboardInterrupt:
        pass
