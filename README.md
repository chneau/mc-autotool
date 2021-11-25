# mc-autotool

## Interesting projects

<https://github.com/SilentChaos512/WIT/tree/1.14-fabric>
<https://github.com/uyjulian/ControlPack/blob/master/cp1.9/src/main/java/ctrlpack/ControlPackMain.java>

## useful commands for testing

`/effect give @s minecraft:resistance 1000000 4 true` > set youself unkillable by monsters

## Refresh code source

Do `make genSources`.  
Then select `gradle.properties`.  
Press Shift+Alt+U.

## Steps to update deps

- https://modmuss50.me/fabric.html for `build.gradle` and `fabric.mod.json`
- https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/ for `build.gradle`
- run `make genSources` and then if there is no errors test with `make test`
