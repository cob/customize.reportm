Crontab:

```shell
crontab -e

# Relatórios: apaga relatorios gerados com mais de dois dias
0 1 * * * find /tmp/*.xlsx -type f -mtime +2 -delete


# Nota: No caso de haver mais do que uma máquina apenas activar
# num dos frontends

# Relatórios: Todas as horas
0 * * * * /opt/others/scripts/generate-report.sh

```