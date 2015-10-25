for D in `find ./minions-contrib -maxdepth 1 -mindepth 1 -type d`
do
    echo Installing $D
    cd $D
    mvn clean package
    cp target/*-jar-with-dependencies.jar ~/.local/share/minions/plugins/.
    cd ../..
done
