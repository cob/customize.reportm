# customize.reportm

Add capabilities to generate reports

## Setup:

### Add the definition to RecordM:

```text
Definition Reports: /others/definitions/definition_reports_v14.json
```

### Configure ReportM Action Pack:

Edit file `integrationm/services/com.cultofbits.intgrationm.service.properties` and add/change the following lines: 
```text
action.names=...,reportm
action.reportm=reportm
action.reportm.reportm.base-url=http://localhost:40580
```

### Crontab

```shell
crontab -e

# Reportm: delete all reports generated more than 2 days
0 1 * * * find /tmp/*.xlsx -type f -mtime +2 -delete

# Reportm: trigger very hour
0 * * * * /opt/others/scripts/generate-report.sh

```