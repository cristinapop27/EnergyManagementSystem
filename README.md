# Energy Management System

Distributed Systems – Assignments 1 and 2. Admins manage users, devices and device-to-client assignment;
clients log in, see their own devices and a chart of each device's hourly energy consumption.

## Architecture

| Container | Role |
|---|---|
| `traefik` | Reverse proxy / API gateway on port 80; validates the JWT (forwardAuth) before forwarding `/api/**` |
| `frontend` | React app served by nginx |
| `auth-service` | Register (publishes `user.created`), login, JWT issuing and validation; keeps credentials in sync with user events – database `auth_db` |
| `user-service` | User profiles: created from `user.created`, read / update / delete through the API (publishes `user.updated` / `user.deleted`) – database `user_db` |
| `device-service` | Device CRUD and assignment, keeps a copy of users (`user_ref`) – database `device_db` |
| `monitoring-service` | Consumes measurements, stores hourly totals, keeps a copy of devices (`device_ref`) – database `monitoring_db` |
| `rabbitmq` | Message broker (AMQP 5672, management UI 15672) |
| `auth-db`, `user-db`, `device-db`, `monitoring-db` | MySQL 8.4, one per service |

The **device simulator** (`simulator/simulator.py`) runs on the host, outside Docker, like a real meter.
Deployment diagram: `deployment_diagram.drawio`.

## Communication

**REST**: frontend → services, through Traefik only. Services never call each other directly.

**RabbitMQ**:

| Exchange | Routing key | Queue (binding) | Consumer | Effect |
|---|---|---|---|---|
| default | `measurements-queue` | `measurements-queue` | monitoring-service | adds the reading to the hourly total |
| `sync-exchange` (topic) | `user.created` (from auth-service, on register) | `sync.user-service` (`user.created`) | user-service | inserts the user profile |
| `sync-exchange` (topic) | `user.updated` / `user.deleted` (from user-service) | `sync.auth-service` (`user.updated`, `user.deleted`) | auth-service | updates username/role / deletes the credential |
| `sync-exchange` (topic) | `user.created` / `user.updated` / `user.deleted` | `sync.device-service` (`user.*`) | device-service | inserts / updates / deletes `user_ref` (deleting a user deletes their devices and publishes `device.deleted` for each) |
| `sync-exchange` (topic) | `device.created` / `device.updated` / `device.deleted` | `sync.monitoring` (`device.*`) | monitoring-service | inserts / updates / deletes `device_ref` (and the device's hourly data) |

- Measurements are point-to-point: each reading is processed once.
- Synchronization is publish-subscribe: each consumer has its own queue, and the topic exchange routes by key. Producers don't know the consumers; each consumer binds only to the keys it needs (device-service `user.*`, monitoring `device.*`, user-service `user.created`, auth-service `user.updated` + `user.deleted`), so no service receives its own events back.
- auth-service generates the user id on register; the same id is used by every service. At startup it seeds the admin only after the user-service and device-service queues exist, so the `user.created` event is not lost.
- Data collection and synchronization share one RabbitMQ container but are separate paths (default exchange + `measurements-queue` vs `sync-exchange`).
- Eventual consistency: a service saves locally and publishes an event. If publishing fails, the change is rolled back. Consumers are idempotent (a repeated event gives the same result), and messages that cannot be processed are rejected without requeue. A new or changed user reaches the other services a few milliseconds later.
- A measurement is JSON `{"timestamp": <epoch ms>, "device_id": "<uuid>", "measurement_value": <kWh>}`, one every 10 minutes. Hours are computed in `Europe/Bucharest` time.

**Chart:** `GET /api/monitoring/devices/{id}/consumption?date=YYYY-MM-DD` returns 24 hourly values.
Admins can read any device. Clients can read only their own devices; monitoring checks the owner using the `X-User-Id` header that Traefik adds after validating the JWT, and answers 403 otherwise.

## Run

Requirements: Docker Desktop, free ports 80 and 8080.

1. Create a `.env` file in the project root:

```env
DEVICE_DB_NAME=device_db
DEVICE_DB_USER=device_user
DEVICE_DB_PASSWORD=change_me
USER_DB_NAME=user_db
USER_DB_USER=user_user
USER_DB_PASSWORD=change_me
AUTH_DB_NAME=auth_db
AUTH_DB_USER=auth_user
AUTH_DB_PASSWORD=change_me
DB_ROOT_PASSWORD=change_me
JWT_SECRET=any-random-string-of-at-least-32-characters
JWT_EXPIRATION_MINUTES=60
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
MONITORING_DB_NAME=monitoring_db
MONITORING_DB_USER=monitoring_user
MONITORING_DB_PASSWORD=change_me
RABBITMQ_USER=ems
RABBITMQ_PASSWORD=change_me
```

2. Build and start everything:

```bash
docker compose up --build -d
```

3. Open http://localhost and log in as `admin` / `admin123` (created automatically at startup).
   Clients can create an account with **Register**. Traefik dashboard: http://localhost:8080/dashboard/,
RabbitMQ UI: http://localhost:15672 (credentials from `.env`).

4. Simulate a meter (Python 3, needs the containers running). Take the device id from the Devices page or `GET /api/devices`:

```bash
pip install -r simulator/requirements.txt
python simulator/simulator.py --device-id <device-uuid>
```

By default it starts now and sends one reading every 10 minutes. To fill a past day quickly:
`--start 2026-11-17T00:00 --interval 2` (simulated time from that moment, one message every 2 real seconds). Stop with Ctrl+C.

Stop: `docker compose down` (keeps data) or `docker compose down -v` (deletes the databases).

## Database dumps

`db-dumps/` contains one dump per database. To load one into a running container:

```bash
docker cp db-dumps/device_db.sql device-db:/tmp/device_db.sql
docker exec device-db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" < /tmp/device_db.sql'
```

(same for `user-db` / `user_db.sql`, `auth-db` / `auth_db.sql` and `monitoring-db` / `monitoring_db.sql`)
