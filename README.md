<p align="center">
  <img src="https://github.com/user-attachments/assets/0d0fd3a7-0b24-4d6a-b196-577ae2385b1b" alt="simplenotify-logo" style="display: block; margin: auto;">
</p>

SimpleNotify
============

Simplification in the use of notifications.

[![Maven Central](https://img.shields.io/badge/Maven_Central-1.0.0-blue)](https://central.sonatype.com/artifact/com.vanskarner.android/simplenotify)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.0-blue.svg?style=flat-square)](https://kotlinlang.org/docs/whatsnew17.html)
[![Androidx.Core](https://img.shields.io/badge/androidx.core-1.13.1-6ab344)](https://developer.android.com/jetpack/androidx/releases/core?hl=es-419#1.13.1)
[![Android Compatibility](https://img.shields.io/badge/Compatible_with-API_24--35-6ab344)](https://github.com/vanskarner/SimpleNotify/actions)

SimpleNotify implements the Fluent API pattern to generate notifications intuitively, eliminating the need to worry about the complexities of different Android API versions.
Integrate notifications into your Android projects with ease, so you can stop wasting time with documentation and focus on what really matters: Your work.

Getting Started
---------------

Use Gradle - with Groovy
```Groovy
dependencies {
  implementation 'com.vanskarner.android:simplenotify:1.0.0'
}
```
Use Gradle - with Kotlin DSL
```Groovy
dependencies {
    implementation("com.vanskarner.android:simplenotify:1.0.0")
}
```

Usage
-----

SimpleNotify uses notification types for its publication, this is the simplest use case:

```kotlin
SimpleNotify.with(context)
    .asBasic { // this: Data.BasicData
        title = "New government scandals"
        text = "Covenants, Armoring, Influence peddling and others"
    }
    .show()
```

<img src="https://github.com/user-attachments/assets/c7e6eef8-3fb5-457f-b280-08731c62431d" alt="Basic usecase" width="380">

See the quick start in the [wiki](https://github.com/vanskarner/SimpleNotify/wiki) for other types of notifications and additional features.

Author
------
### Luis Olazo
- [@vanskarner](https://github.com/vanskarner) and [@luisolazo](https://github.com/LuisOlazo) on **GitHub**
- [@vanskarner](https://x.com/vanskarner) on **Twitter**
- [luisolazo](https://www.kaggle.com/luisolazo) on **Kaggle**
<!-- Luis Olazo - [@vanskarner](https://x.com/vanskarner) on **Twitter** -->

Contributing
------------

There are many ways to contribute:

- Submit bugs and issue reports.
- Help track and prioritize ongoing issues.
- Review and suggest improvements to code changes.
- Update documentation or improve examples.
- Share feedback and ideas for new features.
