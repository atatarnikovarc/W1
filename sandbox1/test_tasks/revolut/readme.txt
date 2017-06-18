Good day, Reviewer!
Below is the description and part of test task completed.
Enjoy by checking it, hope you won't find it annoying=)
Thanks


===How to run tests:

0. Java 7+(JAVA_HOME, PATH are set up), Linux is supposed as the environment to run(otherwise one should
   rewrite 'run-serv.sh' script)
1. Copy the application under test(Revolut_qa_4.3.0.237.apk) to 'data' subfolder
2. Make sure you installed Android SDK properly(ANDROID_HOME, android sdk tools in PATH)
3. Run 'data/run-serv.sh' script
4. Open revolut test module in any IDE, supporting maven and make sure it resolves dependencies
5. Using Android Studio - run default virtual device (API ver. 26)
6. Run 'RevoTest' class as JUnit test

===BUGS

1. When transfer to myself - looping at the adding address, press to 'Next' leads to the same screen,
   no errors detailed
2. No validation Street Address, City, Region again Country
3. Looping at Access Code entering - putting right code leads to nothing  (the app needs to be re-run)
4. Add Beneficiary - to myself no validation of IBAN after on 'Next' button
5. Invalid IBAN: no detailed error message - as a user I'd like to know what is the contradiction
(having feeling - there are much more=]])

===TESTS

1. To myself
-positive test(an option would be to check - how to transfer to that beneficiary, not only existence)
-Invalid IBAN
-Invalid SWIFT
-IBAN is not corresponding to SWIFT
-wrong values for first, last names
-combining coutries and currencies
-combining the beneficiary's address fields(if there are any validation)

2. To another person
-same as above

3. To a business
-same as above