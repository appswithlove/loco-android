# loco-android 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.appswithlove.loco/loco/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.appswithlove.loco/loco)


🇯🇵 🇰🇷 🇩🇪 🇨🇳 🇺🇸 🇫🇷 🇪🇸 🇮🇹 🇷🇺 🇬🇧

This is a gradle plugin for easy update of text strings managed on Loco (localise.biz).

## Instructions

In oder to use the plugin follow those steps:
 
1.Add the following code to you `build.gradle` file in the `root` folder.

```groovy
buildscript {
  repositories {
    //…
    mavenCentral()
  }

  dependencies {
    //…
    classpath 'com.appswithlove.loco:loco:0.2.5'
  }
}
```

2.Apply the plugin in `app/build.gradle`.

```groovy
apply plugin: 'com.appswithlove.loco'
``` 
or
```groovy
plugins {
    ...
    id 'com.appswithlove.loco'
}
``` 


3.Configure the Loco instance in `app/build.gradle`:

Single loco configuration (most common):
```groovy
Loco {
    apiKey = 'YOUR_API_KEY'
    lang = ['de', 'fr'] // add as many languages as you want, they need to exist on localise.biz
    defLang = 'de'
    // one language that will result as the default language and be put in values/strings.xml
    resDir = "$projectDir/src/main/res"
    placeholderPattern = null // optional; regex pattern with leading ~, default -> null
    fileName = "strings" // optional; customise file name
    hideComments = false // optional; hide comments & loco metadata 
    tags = 'Android,!iOS' 
    // optional; filter assets by comma-separated tag names. Match any tag with `*` and negate tags by prefixing with `!`. Whitespaces need to be replaced with _ (e.g. "Some tag" becomes "Some_tag").	 
    fallbackLang = 'en' // optional;, fallback language when not present
    orderByAssetId = false // optional; order assets alphabetically by Asset ID
    status = "translated" // optional; filter assets by status. Negate values by prefixing with !. e.g. "translated", or "!fuzzy".
    saveDefLangDuplicate: false // default: defLang will only be saved in values folder. If set to true, the defLang will also be saved in the specific folder (such as values-en)
}
```

4.Done!

## Usage

After installing the plugin, you should be able to find the Gradle Loco tasks in Android Studio.

```console 
"Gradle Project" Window -> Tasks -> Other -> updateLoco
```

Otherwise, you can call the gradle tasks via command:

```console
./gradlew updateLoco
```

## ⚠️ Keep in mind

Executing `updateLoco` will override all existing `strings.xml` (or other, if custom `fileName`)
files of the given `languages`. Any type of app specific text strings should be placed into a
separate string file, such as `constants.xml`.

## Multiple Loco Projects

Starting from Version `0.2.0`, there is support for multiple Loco configs in the same project. For example, if you need to manage different files from different Loco projects. This can be done via configuration in `app/build.gradle`:

```groovy
import com.appswithlove.loco.LocoConfig

LocoMultiple {
    configs = [
        new LocoConfig().with {
            apiKey: 'FIRST_API_KEY',
            lang: ['de', 'fr'], // add as many languages as you want, they need to exist on localise.biz
            defLang: 'de', // one language that will result as the default language and be put in values/strings.xml
            resDir: "$projectDir/src/main/res",
            placeholderPattern: null, // optional; regex pattern with leading ~, default -> null
            fileName: "strings", // optional; customise file name
            hideComments: false, // optional; hide comments & loco metadata 
            tags: 'Android,!iOS', // optional; filter assets by comma-separated tag names. Match any tag with `*` and negate tags by prefixing with `!`. Whitespaces need to be replaced with _ (e.g. "Some tag" becomes "Some_tag").	 
            fallbackLang: 'en', // optional; fallback language when not present
            orderByAssetId: false // optional; order assets alphabetically by Asset ID
            status: "translated" // optional; filter assets by status. Negate values by prefixing with !. e.g. "translated", or "!fuzzy".
            saveDefLangDuplicate: false, // default: defLang will only be saved in values folder. If set to true, the defLang will also be saved in the specific folder (such as values-en)
            it // Important: Note the explicit mention of it as the return value
        ),
        new LocoConfig().with {
            apiKey: 'SECOND_API_KEY',
            lang: ['de', 'fr'], // add as many languages as you want, they need to exist on localise.biz
            defLang: 'de', // one language that will result as the default language and be put in values/strings.xml
            resDir: "$projectDir/src/main/res",
            placeholderPattern: null, // optional; regex pattern with leading ~, default -> null
            fileName: "strings", // optional; customise file name
            hideComments: false, // optional; hide comments & loco metadata 
            tags: 'Android,!iOS', // optional; filter assets by comma-separated tag names. Match any tag with `*` and negate tags by prefixing with `!`. Whitespaces need to be replaced with _ (e.g. "Some tag" becomes "Some_tag").
            fallbackLang: 'en', // optional;, fallback language when not present
            orderByAssetId: false // optional; order assets alphabetically by Asset ID
            status: "translated" // optional; filter assets by status. Negate values by prefixing with !. e.g. "translated", or "!fuzzy".
            saveDefLangDuplicate: false, // default: defLang will only be saved in values folder. If set to true, the defLang will also be saved in the specific folder (such as values-en)
            it // Important: Note the explicit mention of it as the return value
        )
    ]
}
```

It is important to add the `it` to the end of the creation of your config, if done with `new LocoConfig().with { ... }`


To support multiple import, you can use :

```console 
"Gradle Project" Window -> Tasks -> Other -> updateLocoMultiple
```

or

```console
./gradlew updateLocoMultiple
```

---

## Parameters

The parameter `placeholderPattern` allows to have parameters replaced in text strings. The default value is `null`, therefore no parameter will be replaced. 
If you use a custom pattern, make sure to add a tilde `~` just in front of the pattern, so that it gets recognized as a pattern from gradle.

Example for a pattern: 
`placeholderPattern = ~/\$[^$]*\$/` will replace every parameter in the form `$ANYTEXT$`, `$Any Text$` (start and end with `$`)  with `%s` when updating the Loco strings.
 

## Debug

In order to debug the plugin, `clean` -> `jar` -> `publishJarPublicationToMavenLocal` and connect your android App to the mavenLocal-version of the android plugin by adding the following snipped to your root-folder `build.gradle`

```groovy
buildscript {
	repositories {
		mavenLocal()
		...
	}
	dependencies{
	    classpath 'com.appswithlove.loco:loco:0.2.5'
	    ...
	} 
}
```

After that, call the following script in the terminal of your android app (replace `FLAVOUR`)

```console
./gradlew updateLoco -Dorg.gradle.debug=true --no-daemon
```

Lastly, open the Loco Plugin in Android Studio, add an `Remote` build configuration with `Attach to remote JVM` and run the configuration on debug. Now the gradlew call you triggered before will start running and will hit the break points in the plugin. :) 

Don't forget to republish the plugin-jar when doing changes.
