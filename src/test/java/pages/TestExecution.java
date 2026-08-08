package pages;

import attributes.Execute;


public class TestExecution {

    @Execute
    public void sampleExecute() {
        
        System.out.println( "sample execute");
    }


    @Execute(name = "Some Thing")
    public void runOnStart() {
   
    }
}
