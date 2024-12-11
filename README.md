# SlimeIgnite

An [Ignite](https://github.com/vectrix-space/ignite) mod implementation
of [SlimeLoader](https://github.com/roxymc-net/SlimeLoader) for [Paper](https://papermc.io/).

## Supported versions

| Mod version | Compatible Paper version |
|-------------|--------------------------|
| 0.1         | 1.21.3, 1.21.4           |

## Usage

### Mod installation

Firstly, follow the [Ignite installation instructions](https://github.com/vectrix-space/ignite#install).

Then, navigate to [releases](https://github.com/roxymc-net/SlimeIgnite/releases) or [actions](https://github.com/roxymc-net/SlimeIgnite/actions) page and download the latest mod artifact for your platform.

After downloading, place the mod jar file inside your server's `mods` directory.

Finally, you can simply launch your server just like before and enjoy your slime worlds!
(note: remember to launch the ignite jar, not your platform jar.)

### For developers (API)

SlimeIgnite API is available in our maven repository.

Depending on your target mod version, you would need:

```kts
repositories {
    // for releases
    maven("https://repo.roxymc.net/releases")

    // for snapshots
    maven("https://repo.roxymc.net/snapshots")
}
```

and

```kts
dependencies {
    implementation("net.roxymc:slimeignite-api:VERSION")
}
```

## Compiling

To compile, navigate to project root directory and run:

```shell
./gradlew shadowJar
```