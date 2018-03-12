package eionet.rod.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AuthorisationTest {
	
	 @Test
	  public void instanticateClass() {
		 Authorisation obj = new Authorisation();
	     Assert.assertNotNull(obj);
	  }
	 
	 @Test
     public void instanticateClassWithParams() {
		 List<String> authorisations = new ArrayList<String>();
		 authorisations.add("authorisation1");
		 authorisations.add("authorisation2");
		 Authorisation obj = new Authorisation("userId", authorisations);
		 assertEquals("userId", obj.getUserId());
         assertEquals("authorisation2", obj.getAuthorisations().get(1));
	 }

}
