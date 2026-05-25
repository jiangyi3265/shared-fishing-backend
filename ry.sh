#!/bin/sh
# ./ry.sh start 启动 stop 停止 restart 重启 status 状态
AppName=dy.jar

# JVM参数
JVM_OPTS="-Dname=$AppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"
APP_HOME=`pwd`
LOG_PATH=$APP_HOME/logs/$AppName.log

query(){
    PID=`ps -eo pid=,args= | awk -v app="$AppName" '$0 ~ /[j]ava/ && index($0, app) {print $1; exit}'`
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
		while [ x"$PID" != x"" ]
		do
			sleep 1
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
