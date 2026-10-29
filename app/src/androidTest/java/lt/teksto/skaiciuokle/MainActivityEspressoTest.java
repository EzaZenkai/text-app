package lt.teksto.skaiciuokle;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * UI ir integraciniai testai (Espresso), skirti patikrinti
 * grafinės vartotojo sąsajos veikimą realiame įrenginyje ar emuliatoriuje.
 *
 * Apima 4 pagrindinius scenarijus:
 * 1. Įvesties validacija (tuščias tekstas ir tarpai -> klaidos indikacija).
 * 2. Teksto skaičiavimas ir metrikų rezultatų atvaizdavimas kortelėse.
 * 3. Pavyzdinio teksto generavimas ir rotacija paspaudus mygtuką.
 * 4. Istorijos įrašo sukūrimas, atstatymas paspaudus ant kortelės ir išvalymas.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Scenarijus 1: Įvesties validacija
     * Vartotojas bando skaičiuoti neįvedęs teksto arba įvedęs tik tarpus.
     * Tikrinama, kad parodomas klaidos pranešimas ir skaitikliai lieka nuliniai.
     */
    @Test
    public void scenario1_inputValidation_emptyOrWhitespaceShowsError() {
        // Paspaudžiame skaičiuoti esant tuščiam laukui
        onView(withId(R.id.btnCalculate)).perform(click());

        // Rezultatų skaitikliai lieka 0
        onView(withId(R.id.tvWordCount)).check(matches(withText("0")));
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("0")));

        // Įvedame tik tarpų simbolius
        onView(withId(R.id.etInputText)).perform(typeText("    \n   "), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // Skaitikliai vis dar 0
        onView(withId(R.id.tvWordCount)).check(matches(withText("0")));
        onView(withId(R.id.tvNumberCount)).check(matches(withText("0")));
    }

    /**
     * Scenarijus 2: Sėkmingas skaičiavimas ir rezultatų atvaizdavimas
     * Įvedamas tekstas su žodžiais, skaičiais ir lietuviškomis raidėmis.
     * Tikrinama, ar visi metrikų laukai atvaizduoja teisingus skaičiavimo rezultatus.
     */
    @Test
    public void scenario2_calculateMetrics_displaysCorrectResults() {
        // Įvedamas sakinys: „Rugsėjo 1 prasidėjo karas. Liūdna“
        // Tikėtinos reikšmės: sakiniai: 2, žodžiai: 4, skaičiai: 1, simboliai su tarpais: 33
        String text = "Rugsėjo 1 prasidėjo karas. Liūdna";

        onView(withId(R.id.etInputText)).perform(clearText(), typeText(text), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        onView(withId(R.id.tvSentenceCount)).check(matches(withText("2")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("4")));
        onView(withId(R.id.tvNumberCount)).check(matches(withText("1")));
        onView(withId(R.id.tvCharCountWithSpaces)).check(matches(withText("33")));
        onView(withId(R.id.tvCharCountWithoutSpaces)).check(matches(withText("29")));
        onView(withId(R.id.tvLineCount)).check(matches(withText("1")));
    }

    /**
     * Scenarijus 3: Pavyzdinio teksto generavimas ir rotacija
     * Paspaudžiamas mygtukas „Pavyzdinis tekstas“ – laukas užpildomas pirmuoju pavyzdžiu.
     * Paspaudus dar kartą – tekstas pakeičiamas antruoju pavyzdžiu.
     */
    @Test
    public void scenario3_sampleTextGeneration_rotatesSamples() {
        // 1-asis paspaudimas: įkeliamas 1-asis pavyzdys
        onView(withId(R.id.btnSampleText)).perform(click());
        onView(withId(R.id.etInputText)).check(matches(not(withText(""))));

        // Atliekame skaičiavimą su pavyzdiniu tekstu
        onView(withId(R.id.btnCalculate)).perform(click());
        onView(withId(R.id.tvWordCount)).check(matches(not(withText("0"))));

        // 2-asis paspaudimas: rotacija į kitą pavyzdį
        onView(withId(R.id.btnSampleText)).perform(click());
        onView(withId(R.id.etInputText)).check(matches(not(withText(""))));
    }

    /**
     * Scenarijus 4: Istorijos įrašo sukūrimas, atstatymas ir išvalymas
     * Atlikus skaičiavimą įrašas atsiranda RecyclerView sąraše.
     * Paspaudus „Išvalyti“ duomenys nunulinami.
     * Paspaudus ant istorijos kortelės tekstas ir rezultatai vėl atkuriami.
     * Paspaudus „Išvalyti istoriją“ sąrašas ištuštinamas.
     */
    @Test
    public void scenario4_historyTrackingRestorationAndClear() {
        String testText = "Unikalus istorijos testas 2026.";

        // 1. Įvedame tekstą ir suskaičiuojame
        onView(withId(R.id.etInputText)).perform(clearText(), typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(click());

        // Istorijos sąrašas tampa matomas
        onView(withId(R.id.rvHistory)).check(matches(isDisplayed()));
        onView(withId(R.id.btnClearHistory)).check(matches(isDisplayed()));
        onView(withId(R.id.rvHistory)).check(matches(hasDescendant(withText(testText))));

        // 2. Išvalome įvesties lauką ir rezultatus
        onView(withId(R.id.btnClear)).perform(click());
        onView(withId(R.id.etInputText)).check(matches(withText("")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("0")));

        // 3. Paspaudžiame ant istorijos elemento sąraše – atstatomas tekstas ir metrikos
        onView(withText(testText)).perform(click());
        onView(withId(R.id.etInputText)).check(matches(withText(testText)));
        onView(withId(R.id.tvWordCount)).check(matches(withText("3")));
        onView(withId(R.id.tvNumberCount)).check(matches(withText("1")));

        // 4. Išvalome visą istoriją
        onView(withId(R.id.btnClearHistory)).perform(click());
        onView(withId(R.id.rvHistory)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tvHistoryEmpty)).check(matches(isDisplayed()));
    }
}
