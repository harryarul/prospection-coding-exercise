# Prospection coding exercise #

The exercise is in spring boot. It uses gradle, thymeleaf, google charts and jquery.

So firstly, setup:

1. Setup git and commit your progress `git init`.
2. Import the spring boot project either run the application from your IDE or use the commandline.
3. Run the project with IDE or `./gradlew bootrun` and visit http://localhost:8080 (**NOTE: HTTP not HTTPS**). You will see the screen where everything is wired up, but the numbers are not real. You will fix that.

![chart.png](https://teamprospection.bitbucket.io/prospection-coding-exercise/3895287186-chart.png)
     
## The task at hand: ##
1. Imagine we have a database that has data about individual patients, and what drugs they have purchased, and what day they have purchased it.
    In AnalysisResult.java you will see that it loads a list of patient purchases. It contains the drug purchases for the drugs B and I.
    In PatientPurchase, you will find a list of PatientID, the Drug taken (B or I) and the day of purchase.
    I.e. it is a list showing, that a particular patient bought drug B or I on a particular day.
    In regards to day of purchase, If one record shows "1" for day of purchase, and another record shows "9", then it means the second record was purchased 8 days after the first. 
 
2. In Australia, the government pays for the majority of the cost of medication. (the patient pays part of the cost in the pharmacy eg $35, but the government pays the rest of the cost to the pharmacy directly)
Lets say drugs B and I shouldn't be taken together, i.e. the government does not want to pay for the cost of the drugs if they are taken together for an extended period.
We are doing an audit of the patients to see which patients are taking B and I together and therefore the government is paying for the cost of 2 medications.
You can assume that each time a patient purchases the drug B, they are on treatment with drug B for the next 30 days. Each time a patient purchases drug I, they are on treatment with drug I for the next 90 days.
 
3. I want to know how many patients violated the rule (that you shouldn't take I and B together), and how many patients did not violate the rule.
For the patients that did not violate the rule, I want to know why not.
So essentially we are putting patients into categories based on their behaviour. Here are the categories we want the patients put into:
    * **Patients that violated** by clearly taking B and I together. You can tell that the patients are taking both drugs together. I.e. the purchases overlap. Example: their pattern may look something like B, I, I, B, B, B, I etc
    * **Patients that did not violate**, because they never took B and I together. They may have taken both B and I at some stage, but there is a gap between using the 2 drugs. Example: the pattern will look something like B, B, B, a gap , I, I, I etc. (Remember, B lasts for 30 days, and I lasts for 90 days. So a gap means they are not on any medication)
    * **Patients that switched** into B or switched into I. Here using 30 days for B and 90 days for I, it will look like the patients have taken both drugs for a time period, but actually they shouldn't be interpreted as taking both drugs, and should be interpreted as switching drugs from B to I or from I to B. The pattern will look something like I, I, I, B, B, B or I, I, I, B, B, B, B, B etc. Eg it will only overlap for that last purchase before switching drugs.
        * Note that until the patient comes out clean(not on treatment on either drug), only one switch is allowed. A streak of I, I, I, B, B, B, B, I, I is a violation, while I, I, I, B, B, a gap, I, I, B, B is 2 separate cases of I to B switches.
    * **Patients that seem to trial** B/I as a once off - the pattern will look something like I, I, I, I, I, B, I, I, I, I or B, B, B, B, B, I, B, B, B, B. Because they are only trialing the other drug (took only once), we won't count them as taking both drugs at once.
        * Note: similar to switches, a patient is only allowed for one trial during one streak. I.e. I, I, B, I, I, I, I, B, I is considered a violation rather than trials. The patient can have another trial after he/she comes out clean.
    * Switch, trial and violation does not carry over to the next streak once the patient comes out clean. I.e. I, I, I, B, B, a gap, B, I, I is not a violation. Rather, it is an I to B switch followed by a B to I switch. 
    * A case of single alternative drug at the end of a streak is considered a switch. I.e. I, I, I, B is considered a I to B switch rather than B trial, even if the patient only took B once. Similarly, a single purchase of different drug at the beginning of a streak is also considered a switch. I.e. I, B, B, B is an I to B switch.
    * One patient will only be put into one category. When a patient falls under more than one category, the actual category he/she belongs to should be decided as follows:
        1. If at any point of the purchase history there is a violation, the patient is considered violation.
        1. If throughout the purchase history, the patient did not have any switch, trial or violation, then he/she is considered never took B and I together.
        1. If the patient has more than one case of switch and/or trial, he/she's category is the first switch/trial category he/she had. For example, I, I, I, B, B, a gap, B, B, I, B has an I to B switch, followed by a I trial. The patient is considered I to B switch, since it happend before I trial.

4. When the result is worked out, they will be reflected on the page. 
 
5. Next I want to see the patients under a particular category when a slice on the pie/donut diagram is clicked.
    This is open to interpretation as to how you want to represent this.
    For example, this is one option: when a pie/donut slice is clicked, a table below the pie/donut loads the patients that fall under that category and displays their purchase pattern like:
    patient 1: I (day1), I (day 27), I (day 45)
    patient 2: I (day1), I (day 5), B (day 90), B (day 135), I (day 170) etc
 
6. Then instead of representing patient purchases in a table, show them in some sort of timeline:

    ![chart.png](https://teamprospection.bitbucket.io/prospection-coding-exercise/timeline.png)

    *(generic example of a timeline)*
 
7. Highlight the purchases that caused a patient to be in the category that he/she is in. Eg if its the switch category, then highlight the switch purchases. If its the clearly violates category then highlight the violating purchases.

## Testing

You can run the tests with `./gradlew test`. at first, BIAnalysisServiceTest is expected to fail. JUnit, Mockito and AssertJ are all available.

## Submission

Zip up the directory and send it to us.
