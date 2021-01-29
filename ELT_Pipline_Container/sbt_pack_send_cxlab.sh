#! /bin/bash 

jar_file=$(pwd)/target/scala-2.11/*.jar


if [ -z $1 ]
then
	echo "ERROR: the jar file destination is empty..."
	exit 5
fi

if(test -f $jar_file)
then 
	rm -f $jar_file
	echo "OLD jar file is removed.."
	echo "current path: $(pwd)"
else	
	echo "No jar file is packed yet.'sbt package'"
	echo -n "current path: "
	echo $(pwd)
fi

echo "sbt is packaging the NEW jar file..."
sbt package 
echo "sbt done..."

echo "jar is sent to cloudxlab and check the remote system for the jar files..."

if (test -f $jar_file)
then
		sshpass -p "NFA5EIIC" scp $jar_file kyang35681@f.cloudxlab.com:$1

echo "file is sent..."

fi
