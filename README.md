# customize.Reportm

Add capabilities to generate reports

## Setup:

### Add the definition to RecordM:

```text
Reports: /others/definitions/definition_reports_v14.json
```

### Crontab

```shell
crontab -e

# Reportm: delete all reports generated more than 2 days
0 1 * * * find /tmp/*.xlsx -type f -mtime +2 -delete

# Reportm: trigger very hour
0 * * * * /opt/others/scripts/generate-report.sh

```