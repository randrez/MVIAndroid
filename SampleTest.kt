

class SampleTest {

    //create variables that you will use in your testing typically in unit tests this includes

    //the class you are testing, THIS SHOULD NEVER BE A MOCKK

    //some dependencies that you need to mockk the behavior of

    @Test //every test needs this annotation, and a public function
    fun runSomething(): { //the name doesn't need the word test in it, just descriptive
        //always include three sections

        //setup is where you build your mocks, setup objects and observers etc

        //exectue, is where you fire off the either user or data action

        //verify, is where you verify that the results are consistent with your understanding

    }

    //create private methods to build out reusuable test objects

    //create reusable verify methods that test the data you are making acrros multiple scenarios

    //ALWAYS try the negative scenario
}