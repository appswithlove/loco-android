# loco-android

This is a gradle plugin for easy update of text strings managed on Loco (localize.biz).

## Instructions

In oder to use the plugin follow those steps:
 
1.Add the following code to you `build.gradle` file in the `app` folder.

```groovy
buildscript {
  repositories {
    //…
    jcenter()
  }

  dependencies {
    //…
    classpath 'com.appswithlove.loco:loco:0.0.1'
  }
}
```

2.Apply the plugin in `build.gradle` in `app` folder.

```groovy
apply plugin: 'com.appswithlove.loco'
```

3. Configure the Loco instance:

```groovy
Loco {
    apiKey = 'YOUR_API_KEY'
    lang = ['de', 'fr'] //add as many languages as you want, they need to exist on Localize.biz
    defLang = 'de' //one language that will result as the default language and be put in values/strings.xml
    resDir = "$projectDir/src/main/res"
}

```

4.Done! 

## Usage
After installing the plugin, you should be able to find the Gradle Updraft tasks in Android Studio. The naming is always `updraft` + buildVariant. There is 1 task for every available buildVariant. 
``` 
"Gradle Project" Window -> Tasks -> Other -> updateLoco
```
Otherwise, you can call the gradle tasks via command: 
```
./gradlew updateLoco
```

---

**Keep in mind!**

This will override all existing `values.xml` files of the given `languages`. Any type of app specific text strings should be placed into a separate string file, such as `constants.xml`.

---


# Parameters

The plugin allows to have parameters in text strings. Every parameter in the form `$ANYTEXT$`, `$Any Text$` (start and end with `$`) will be translated to `%s` when updating Loco.




## Debug

In order to debug the plugin, `clean` -> `jar` -> `publishJarPublicationToMavenLocal` and connect your android App to the mavenLocal-version of the android plugin by adding the following snipped to your root-folder `build.gradle`

```
buildscript {
	repositories {
		mavenLocal()
		...
	}
	dependencies{
	    classpath 'com.appswithlove.loco:loco:0.0.1'
	    ...
	} 
}

```

After that, call the following script in the terminal of your android app (replace `FLAVOUR`)

```
./gradlew updateLoco -Dorg.gradle.debug=true --no-daemon
```

Lastly, open the Loco Plugin in Android Studio, add an `Remote` build configuration with `Attach to remote JVM` and run the configuration on debug. Now the gradlew call you triggered before will start running and will hit the break points in the plugin. :) 

Don't forget to republish the plugin-jar when doing changes.
