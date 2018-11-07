## Misc. Tuning of the sample

Some of this could/should be factored out into various parameters:

### Modifying the Number of Tweets Displayed

Set "startingPoint" and "endingPoint" to your desired values in [TweetService.java](../src/main/java/application/fetcher/TweetService.java).

### Temporarily Disable Sentiment Analysis

Set useWatson to **false** in the processor.

In [TweetFileProcessing.xml](../src/main/resources/META-INF/batch-jobs/TweetFileProcessing.xml) edit this property within the job XML:

```
   <properties>
      ...
        <property name="useWatson" value="true"/>
   </properties>
   
```


## Modifying the Front End

Go to the reacter folder and ```npm install```. Run ```npm start``` to preview your changes.  The react development server uses port 3000 by default, but will automatically proxy any api calls to Fetcher's REST API on port 9080 and transpile .scss files to .css for you. When you have made your changes, do a ```npm run build``` to compile. Move the contents of reacter's build folder into the webapp folder, overwriting the older files.  

### Modification process in detail 

**Prereq:**  Install npm

0. Do npm install (only need to once)

     ` npm install`

1. Do reacter build

     `cd reacter; npm run build; cd --`

2. Delete old 

     `git rm -r src/main/webapp/`

3. Copy over reacter build

    `mkdir src/main/webapp;  cp -r reacter/build/* src/main/webapp/ `

4. Add new to Git

    `git add src/main/webapp/`

5. Now add back .gitignore in src/main/webapp 
(TODO - better procedure)

Now proceed to do a commit, etc.

## NPM Audit

As seen [here](https://docs.npmjs.com/getting-started/running-a-security-audit#running-a-security-audit-with-npm-audit), you can run `npm audit` and `npm audit fix` to resolve security issues with underlying dependencies.

**TODO:**  Elaborate on this process.
