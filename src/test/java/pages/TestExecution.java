package pages;

import attributes.Execute;

import reflection.HostService;

import java.security.PublicKey;


public class TestExecution {

    @Execute
    public void sampleExecute() {
        
        System.out.println( "sample execute");
    }


    @Execute(name = "Some Thing")
    public void runOnStart() {
   
    }
}
