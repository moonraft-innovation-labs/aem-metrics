# aem-metrics

This repo holds the example code for medium.com story

## How to obtain client id for unsplash API
To use Unsplash developer API, you need to Join Unsplash and generate an API key. For more details about how to create a developer account and API key, read Unsplash Documentation
Update com.mysite.core.service.impl.UnsplashServiceImpl-config.xml with client id.

## How to build

If you have a running AEM instance you can build and package the whole project and deploy into AEM with

    mvn clean install -PautoInstallPackage -Padobe-public

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
