# loco-android 

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c09a5a2d2d6444b38b092bdaa94aa964)](https://app.codacy.com/app/yannickpulver/loco-android?utm_source=github.com&utm_medium=referral&utm_content=appswithlove/loco-android&utm_campaign=Badge_Grade_Dashboard)
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
    classpath 'com.appswithlove.loco:loco:0.1.9'
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

```groovy
Loco {
    apiKey = 'YOUR_API_KEY'
    lang = ['de', 'fr'] // add as many languages as you want, they need to exist on localise.biz
    defLang = 'de' // one language that will result as the default language and be put in values/strings.xml
    resDir = "$projectDir/src/main/res"
    placeholderPattern = null // optional, regex pattern with leading ~, default -> null
    hideComments = false // optionally hide comments & loco metadata 
    tags = 'Android,!iOS' // optional, filter assets by comma-separated tag names. Match any tag with `*` and negate tags by prefixing with `!`	 
    fallbackLang = 'en' // optional, fallback language when not present
    orderByAssetId = false // optionally order assets alphabetically by Asset ID
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

---

## ⚠️ Keep in mind

Executing `updateLoco` will override all existing `values.xml` files of the given `languages`. Any type of app specific text strings should be placed into a separate string file, such as `constants.xml`.

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
	    classpath 'com.appswithlove.loco:loco:0.1.9'
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
