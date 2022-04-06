# artem-gizzatullin

### This doc contains test-cases for monefy app.

`pets-api-tests` folder contains automated tests for api-app, there is detailed `Readme` in the folder

# Monefy manual test:

###1. Exploratory testing

I will put just a kind of check-list with high-level descriptions
* Basic functionality (records CRUD):
    * adding expense / income records (checking balance too)
    * editing records
    * removing records
    * listing (filtering by dates / agregation)
    * searching records
* Synchronization
    * sync with Dropbox
    * sync with Google Drive
* Data backup / restore
    * save backup
    * restore backup
    * clear?
* Common settings
    * localization (could be very important when support few langs)
    * Currency change
    * 1 day settings
    * passcode
* Balance settings
    * budget mode
    * carry over
    * recurring records
* Accounts
    * accountds CRUD
    * transfer between accounts

###2. Bugs
I didn't meet any serious bug, maybe only some animation could be discussable (for example, after adding new records it looks strange sometimes)
I guess, the most interesting things could be in the custom categories, few currency budget and synchronization things, but it requires paid account

###3. Priority

There are few question we should answer for understand the priority of things to tests:
* basic functionality
* how the app generates money for us (paid features, different license level, advertising, etc)

So, in the case of Monefy app, im my opinion, there are few key things should to be checked with maximum priority:
* Basic functionality of the free app (expense / income, balance, categorization, reports aggregation). If something in basic things works wrong, I don't think anyone will buy paid account
* Paid functions (custom categories, sync, custom currency, etc). Because of paid functions brings money to company, we should satisfy customers who paid for that
* All the other things

###4. Time planned
Because of simplicity of the app, it could take not too much time for test, about 3-4 hours for tests all things for the free account.
Meanwhile, it is a mobile app, that means, we should test it with a big count of different devices for 2 platform.
In this case, most part of the time will be spent for device compatibility testing

###5. Risks

This is a little bit strange section, because I'm not sure what should be placed here.
From one hand, if we talk about SD process at all, there is a risk every time when new version released, so, it requires kind of regression testing.
Because of this, we need to retest all the things that works previously for be sure nothing has been broken after new version released.

From the other hand, the app exists since 2014 and still has good reviews and score in Google Play, so, I guess, their team doing all this things good :) 
Also, mobile specific thing is a device compatibility, so, it could be a big risk if we do not test it for a huge number of devices