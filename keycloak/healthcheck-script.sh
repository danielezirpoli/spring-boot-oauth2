#!/bin/bash
exec 3<>/dev/tcp/localhost/9090

echo -e "GET /health/ready HTTP/1.1\nhost: localhost:9090\n" >&3

timeout --preserve-status 1 cat <&3 | grep -m 1 status | grep -m 1 UP
ERROR=$?

exec 3<&-
exec 3>&-

exit $ERROR