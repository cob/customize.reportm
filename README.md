# customize.reportm

Add capabilities to generate reports

## Setup:

### Add the definition to RecordM:

```text
Definition Reports: /others/definitions/definition_reports_v1.json
```

### Create permission:

```text
actions:execute:reportm-on-done
product: integrationm
```

### Configure ReportM Action Pack:

Edit file `/etc/integrationm/services/com.cultofbits.intgrationm.service.properties` and add/change the following lines: 
```text
action.names=...,reportm,email
action.reportm=reportm

action.email=email
action.email.email.default-sender=<the email address as default sender> (Required)
```

### Crontab

```shell
crontab -e

# Reportm: delete all reports generated more than 2 days
0 1 * * * find /tmp/*.xlsx -type f -mtime +2 -delete

# Reportm: trigger very hour
0 * * * * /opt/others/scripts/generate-report.sh

```

## Build & test

```bash
./run-tests.sh
```

### Definition Upgrades:

See [readme](./others/customize.reportm/README.MD)