#!/bin/sh
# ./ry.sh start 启动 stop 停止 restart 重启 status 状态
AppName=dy.jar

# JVM参数
JVM_OPTS="-Dname=$AppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"
APP_HOME=`pwd`
LOG_PATH=$APP_HOME/logs/$AppName.log

if [ -f "$APP_HOME/.env.production" ]; then
    set -a
    . "$APP_HOME/.env.production"
    set +a
elif [ -f "$APP_HOME/.env" ]; then
    set -a
    . "$APP_HOME/.env"
    set +a
fi

: "${CORS_ALLOWED_ORIGINS:=https://ht.diaoyuus.cn,https://www.diaoyuus.cn}"
export CORS_ALLOWED_ORIGINS

query(){
    PID=`ps -eo pid=,comm=,args= | awk -v app="$AppName" '$2 ~ /^java/ && index($0, app) {print $1; exit}'`
}

if [ "$1" = "" ];
then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ];
then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

function start()
{
    query

	if [ x"$PID" != x"" ]; then
	    echo "$AppName is running..."
	else
		mkdir -p "$APP_HOME/logs"
		nohup java $JVM_OPTS -jar $AppName > "$LOG_PATH" 2>&1 &
		echo "Start $AppName success..."
	fi
}

function stop()
{
    echo "Stop $AppName"

	query
	if [ x"$PID" != x"" ]; then
		kill -TERM $PID
		echo "$AppName (pid:$PID) exiting..."
		WAIT_SECONDS=0
		while [ x"$PID" != x"" ]
		do
			if [ "$WAIT_SECONDS" -ge 60 ]; then
				echo "$AppName did not exit after ${WAIT_SECONDS}s; killing it..."
				kill -KILL $PID 2>/dev/null || true
			fi
			if [ "$WAIT_SECONDS" -ge 75 ]; then
				echo "$AppName failed to stop."
				exit 1
			fi
			sleep 1
			WAIT_SECONDS=$((WAIT_SECONDS + 1))
			query
		done
		echo "$AppName exited."
	else
		echo "$AppName already stopped."
	fi
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
    query
    if [ x"$PID" != x"" ];then
        echo "$AppName is running..."
    else
        echo "$AppName is not running..."
    fi
}

case $1 in
    start)
    start;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    *)

esac
