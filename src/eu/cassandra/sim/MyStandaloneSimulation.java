/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package eu.cassandra.sim;

import java.util.HashMap; 
import java.util.TreeMap;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.GaussianMixtureModels;
import eu.cassandra.sim.math.Histogram;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.math.Uniform;
import eu.cassandra.sim.model_library.ConsumptionModelsLibrary;
import eu.cassandra.sim.model_library.DistributionsLibrary;
import eu.cassandra.sim.model_library.PricingPoliciesLibrary;
import eu.cassandra.sim.utilities.Constants;

/**
 * 
 * 
 * @author Fani A. Tzima (fani [dot] tzima [at] iti [dot] gr)
 * 
 */
public class MyStandaloneSimulation extends Simulation{
	
	public MyStandaloneSimulation(String aresources_path, String adbname, int seed, boolean useDerby) {
		super(aresources_path, adbname, seed, useDerby);
	}
	
	@Override
	public Vector<Installation> setupScenario()
	{
//		return  setupSimpleDynamicScenario();
		return setupSimpleStaticScenario();
	}
	
	private Vector<Installation> setupSimpleDynamicScenario()
	{
		
//		//set up simulation parameters
//		String scenarioName = "Scenario1";
//  		String responseType = "None"; 		
//  	    String locationInfo ="Thessaloniki";
//  	    int numOfDays = 3; 					
//  	    int startDateDay = 5;
//	    int startDateMonth = 4;
//	    int startDateYear = 2014;
//	    int mcruns = 5;
//  		double co2 = 2; 
//  		String setup = "static"; 							
//	    SimulationParams simParams = new SimulationParams(responseType, scenarioName, locationInfo, numOfDays, startDateDay,  startDateMonth, startDateYear);
//	    simParams.setMcruns(mcruns);
//	    simParams.setCo2(co2);
//	    simParams.setSetup(setup);
//	    this.simulationWorld =simParams;
//	    this.mcruns = this.simulationWorld.getMcruns();
//		this.co2 = this.simulationWorld.getCo2();
//		this.numOfDays = this.simulationWorld.getNumOfDays();
//		this.setup = this.simulationWorld.getSetup();
		
		// set up demographic data
		int numOfInstallations = 2;		
		TreeMap<String,Double>instGen = new TreeMap<String,Double>();			
		instGen.put("inst1", 1.0);
		TreeMap<String,Double> applGen = new TreeMap<String,Double>();				
		applGen.put("appl21", 1.0);
		applGen.put("appl3", 1.0);
		applGen.put("appl5", 1.0);
		applGen.put("appl4", 1.0);
		applGen.put("appl2", 1.0);
		applGen.put("appl1", 1.0);
		TreeMap<String,Double> personGen = new TreeMap<String,Double>();				
		personGen.put("person1", 0.7);
		personGen.put("person2", 0.3);
		this.demographics = new DemographicData("lala", "description", "type", numOfInstallations, instGen, personGen, applGen);
		
		return null;
		
	}
	
	private Vector<Installation> setupSimpleStaticScenario()
	{
		//setup simulation parameters
		String scenarioName = "Scenario1";
  		String responseType = "None"; 		
  	    String locationInfo ="Thessaloniki";
  	    int numOfDays = 3; 					
  	    int startDateDay = 5;
	    int startDateMonth = 4;
	    int startDateYear = 2014;
	    int mcruns = 5;
  		double co2 = 2; 
  		String setup = "static"; 			
  		
	    this.simulationWorld = new SimulationParams(responseType, scenarioName, locationInfo, numOfDays, 
	    		startDateDay,  startDateMonth, startDateYear, mcruns, co2, setup);
	    
		//set up pricing policy
	    String pricingType = "AllInclusivePricing"; 			
		int billingCycle = 120;  					
		double fixedCharge = 15;				
		PricingPolicy.Builder builderPP = new PricingPolicy.Builder(pricingType, fixedCharge, billingCycle);			
		builderPP.allInclusivePricing(100, 50, 100);
		PricingPolicy pricPolicy = builderPP.build();
		this.pricing = pricPolicy;
		
		//set up the simulation entities
		Vector<Installation> installations = new Vector<Installation>();
  		
  		//Create the installation
		String instName = "Fani's house";			
		String instID= "inst1";								
		String instDescription = "Sample installation";	
		Installation inst = new Installation.Builder(instID, instName, instDescription, instDescription, this.pricing, this.baseline_pricing).build();
		
		//Create the appliances
		HashMap<String, Appliance> appliances = new HashMap<String,Appliance>();
		
		String applName ="Cleaning Washing Machine";
		String appliID = "appl1";
		String applDescription = "Description of Cleaning Washing Machine";
		String applType = "Cleaning";
		double applStandByCons = 0;
		boolean applIsBase = false;
		ConsumptionModel consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("p");
		ConsumptionModel consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("q");
		Appliance app1 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		appliances.put(appliID, app1);
		inst.addAppliance(app1);
		
		applName ="Cleaning Vacuum Cleaner";
		appliID = "appl2";
		applDescription = "Description of Cleaning Vacuum Cleaner";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("q");
		Appliance app2 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		appliances.put(appliID, app2);
		inst.addAppliance(app2);
		
		// Create the people
		String personName = "Fani";
		String personID = "person1";
		String personDesc ="Single person";	
		String personType ="Girl";	
		double awareness= 0.8;
		double sensitivity=0.3;
		Person person = new Person.Builder(personID, personName, personDesc, personType, inst, awareness, sensitivity).build();
		
		// Create the activities
		String activityName = "Person Cleaning Activity"; 
		String activityID = "act1";
		String activityDesc = "Person Cleaning Activity"; 
		String activityType = "Cleaning"; 
		Activity act1 = new Activity.Builder(activityID, activityName, activityDesc, activityType, this.simulationWorld).build();
		
		String actmodDayType = "any";   
		
		ProbabilityDistribution durDist = new Gaussian(1, 1, true); 			
//		durDist.precompute(0, Constants.MINUTES_PER_DAY-1, Constants.MINUTES_PER_DAY);
		act1.addDuration(actmodDayType, durDist);
		
		ProbabilityDistribution startDist = new Histogram(DistributionsLibrary.getStartTimeHistForCleaning());
		act1.addStartTime(actmodDayType, startDist);
		
		double[] v4 = {0.25,0.375,0.25,0,0,0,0,0.125};
		ProbabilityDistribution timesDist = new Histogram(v4);
		act1.addTimes(actmodDayType, timesDist);
		
		boolean shiftable = false;
		act1.addShiftable(actmodDayType, shiftable);
		boolean exclusive = true;
		act1.addConfig(actmodDayType, exclusive);
		
		String[] containsAppliances = {"appl1", "appl2"};
		act1.addAppliances(containsAppliances, appliances, actmodDayType);
		
		person.addActivity(act1);
		
		inst.addPerson(person);	
		
		installations.add(inst);
		
  		return installations;
	}
	
	private Vector<Installation> setupProjectDynamicDynamicScenario_Installations(SimulationParams simParams)
	{
  		Vector<Installation> installations = new Vector<Installation>();
  		
  		//Create the installation
		String instName = "Milioudis Base";			// installation names
		String instID= "inst1";								// installation ids
		String instDescription = "Milioudis Base";	// installation descriptions
		String instType = "lala1";							// installation types
		
		Installation inst = new Installation.Builder(instID, instName, instDescription, instDescription, this.pricing, this.baseline_pricing).build();
		
		// Create the appliances
		HashMap<String,Appliance> existing = new HashMap<String,Appliance>();
		
		String applName ="Cleaning Washing Machine";
		String appliID = "appl1";
		String applDescription = "Description of Cleaning Washing Machine";
		String applType = "Washing";
		double applStandByCons = 0;
		boolean applIsBase = false;
		ConsumptionModel consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("p");
		ConsumptionModel consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("q");
		Appliance app1 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app1);
		inst.addAppliance(app1);
		
		applName ="Lighting Lighting 0";
		appliID = "appl2";
		applDescription = "Description of Lighting Lighting 0";
		applType = "Lighting";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForLighting("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForLighting("q");
		Appliance app2 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app2);
		inst.addAppliance(app2);
		
		applName ="Cleaning Vacuum Cleaner 0";
		appliID = "appl3";
		applDescription = "Description of Cleaning Vacuum Cleaner 0";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("q");
		Appliance app3 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app3);
		inst.addAppliance(app3);
		
		applName ="Cleaning Water Heater";
		appliID = "appl4";
		applDescription = "Description of Cleaning Water Heater";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWaterHeater("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWaterHeater("q");
		Appliance app4 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app4);
		inst.addAppliance(app4);

		applName ="Cleaning Vacuum Cleaner 1";
		appliID = "appl5";
		applDescription = "Description of Cleaning Vacuum Cleaner 1";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner2("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner2("q");
		Appliance app5 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app5);
		inst.addAppliance(app5);
		
		applName ="Cleaning Washing Machine";
		appliID = "appl21";
		applDescription = "Description of Cleaning Washing Machine";
		applType = "Washing";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("q");
		Appliance app21 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app21);
		inst.addAppliance(app21);
		
		
		// Create the people
		String personName = "Nikos";
		String personID = "person1";
		String personDesc ="Person";	
		String personType ="Boy";	
		double awareness= 0.8;
		double sensitivity=0.3;
		Person person = new Person.Builder(personID, personName, personDesc, personType, inst, awareness, sensitivity).build();
		
		// Create the activities
		String activityName = "Cleaning"; 
		String activityID = "act1";
		String activityDesc = "Person Cleaning Activity"; 
		String activityType = "lala"; 
		Activity act1 = new Activity.Builder(activityID, activityName, activityDesc, activityType, simParams).build();
		
		String actmodDayType = "any";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		
		ProbabilityDistribution durDist = new Gaussian(1, 1, true); 			// Normal Distribution: mean = 1, std = 1
//		durDist.precompute(0, 1439, 1440);
		act1.addDuration(actmodDayType, durDist);
		
		ProbabilityDistribution startDist = new Histogram(DistributionsLibrary.getStartTimeHistForCleaning());
		act1.addStartTime(actmodDayType, startDist);
		
		double[] v4 = {0.25,0.375,0.25,0,0,0,0,0.125};
		ProbabilityDistribution timesDist = new Histogram(v4);
		act1.addTimes(actmodDayType, timesDist);
		
		boolean shiftable = false;
		act1.addShiftable(actmodDayType, shiftable);
		boolean exclusive = true;
		act1.addConfig(actmodDayType, exclusive);
		
		String[] containsAppliances = {"appl1", "appl3", "appl4", "appl5"};
//		String[] containsAppliances = {"appl1"};
		// add appliances
		act1.addAppliances(containsAppliances, existing, actmodDayType);
		
		person.addActivity(act1);
		
		
		activityName = "Lighting";
		activityID = "act2";
		activityDesc = "Person Lighting Activity";
		Activity.Builder actBuilder = new Activity.Builder(activityID, activityName, activityDesc, "", simParams);
		
		actmodDayType = "any";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		
		ProbabilityDistribution durDist2 = new Gaussian(1, 1, true); 				
//		durDist2.precompute(0, 1439, 1440);
		actBuilder.duration(actmodDayType, durDist2);
		
		ProbabilityDistribution startDist2 = new Histogram(DistributionsLibrary.getStartTimeHistForLighting());
		actBuilder.startTime(actmodDayType, startDist2);
		
		double[] v2a = {0.22222,0.33333,0.44444};
		ProbabilityDistribution timesDist2 = new Histogram(v2a);
		actBuilder.times(actmodDayType, timesDist2);
		
		shiftable = false;
		actBuilder.shiftable(actmodDayType, shiftable);
		
		Activity act2 = actBuilder.build();
		exclusive = true;
		act2.addConfig(actmodDayType, exclusive);
		
		String[] containsAppliances2 = {"appl2"};
		// add appliances
		act2.addAppliances(containsAppliances2, existing, actmodDayType);
		
		person.addActivity(act2);
		
		inst.addPerson(person);	
		
		// Create the people
		personName = "Fani";
		personID = "person2";
		personDesc ="Person";	
		personType ="Girl";	
		awareness= 0.9;
		sensitivity=0.7;
		Person person2 = new Person.Builder(personID, personName, personDesc, personType, inst, awareness, sensitivity).build();
		
		// Create the activities
		activityName = "Cleaning"; 
		activityID = "act21";
		activityDesc = "Person Cleaning Activity"; 
		activityType = "lala"; 
		Activity act21 = new Activity.Builder(activityID, activityName, activityDesc, activityType, simParams).build();
		
		String[] containsAppliances21 = {"appl21"};
		
		actmodDayType = "weekends";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 	
		double[] w = {0.7, 0.3};
     	double[] means = {480, 1200};
     	double[] stds = {40, 60};
		ProbabilityDistribution durDist3 = new GaussianMixtureModels(w.length, w, means, stds, true);
//		durDist3.precompute(0, 1439, 1440);
		act21.addDuration(actmodDayType, durDist3);
		ProbabilityDistribution startDist3 = new Histogram(DistributionsLibrary.getStartTimeHistForCleaning());
		act21.addStartTime(actmodDayType, startDist3);
		double[] v42 = {0.25,0.375,0.25,0,0,0,0,0.125};
		ProbabilityDistribution timesDist3 = new Histogram(v42);
		act21.addTimes(actmodDayType, timesDist3);
		act21.addShiftable(actmodDayType, shiftable);
		act21.addConfig(actmodDayType, exclusive);
		act21.addAppliances(containsAppliances21, existing, actmodDayType);
		
		actmodDayType = "weekdays";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		double[] durDist4V = {100.0, 50.0, 200.0};
		ProbabilityDistribution durDist4 = new Histogram(durDist4V);
		act21.addDuration(actmodDayType, durDist4);
		ProbabilityDistribution startDist4 = null;
		double from = 100;
		double to = 400;
		startDist4 = new Uniform(from, to, true);	
		act21.addStartTime(actmodDayType, startDist4);
		double[] timesDist4V = {0.2, 0.3, 0.5, 0.4};
		ProbabilityDistribution timesDist4 = new Histogram(timesDist4V);
		act21.addTimes(actmodDayType, timesDist4);
		act21.addShiftable(actmodDayType, shiftable);
		act21.addConfig(actmodDayType, exclusive);
		act21.addAppliances(containsAppliances21, existing, actmodDayType);
		
		person2.addActivity(act21);
		
		inst.addPerson(person2);	
		
		installations.add(inst);
		
  		return installations;
	}
	
	private Vector<Installation> setupFaniTestScenario1_Installations(SimulationParams simParams)
	{
  		Vector<Installation> installations = new Vector<Installation>();
  		
  		//Create the installation
		String instName = "Milioudis Base";			// installation names
		String instID= "inst1";								// installation ids
		String instDescription = "Milioudis Base";	// installation descriptions
		String instType = "lala1";							// installation types
		
		Installation inst = new Installation.Builder(instID, instName, instDescription, instDescription, this.pricing, this.baseline_pricing).build();
		
		// Create the appliances
		HashMap<String,Appliance> existing = new HashMap<String,Appliance>();
		
		String applName ="Cleaning Washing Machine";
		String appliID = "appl1";
		String applDescription = "Description of Cleaning Washing Machine";
		String applType = "Washing";
		double applStandByCons = 0;
		boolean applIsBase = false;
		ConsumptionModel consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("p");
		ConsumptionModel consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("q");
		Appliance app1 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app1);
		inst.addAppliance(app1);
		
		applName ="Lighting Lighting 0";
		appliID = "appl2";
		applDescription = "Description of Lighting Lighting 0";
		applType = "Lighting";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForLighting("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForLighting("q");
		Appliance app2 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app2);
		inst.addAppliance(app2);
		
		applName ="Cleaning Vacuum Cleaner 0";
		appliID = "appl3";
		applDescription = "Description of Cleaning Vacuum Cleaner 0";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner1("q");
		Appliance app3 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app3);
		inst.addAppliance(app3);
		
		applName ="Cleaning Water Heater";
		appliID = "appl4";
		applDescription = "Description of Cleaning Water Heater";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWaterHeater("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWaterHeater("q");
		Appliance app4 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app4);
		inst.addAppliance(app4);

		applName ="Cleaning Vacuum Cleaner 1";
		appliID = "appl5";
		applDescription = "Description of Cleaning Vacuum Cleaner 1";
		applType = "Cleaning";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner2("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForVacuumCleaner2("q");
		Appliance app5 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app5);
		inst.addAppliance(app5);
		
		
		// Create the people
		String personName = "Nikos";
		String personID = "person1";
		String personDesc ="Person";	
		String personType ="Boy";	
		double awareness= 0.8;
		double sensitivity=0.3;
		Person person = new Person.Builder(personID, personName, personDesc, personType, inst, awareness, sensitivity).build();
		
		// Create the activities
		String activityName = "Cleaning"; 
		String activityID = "act1";
		String activityDesc = "Person Cleaning Activity"; 
		String activityType = "lala"; 
		Activity act1 = new Activity.Builder(activityID, activityName, activityDesc, activityType, simParams).build();
		
		String actmodDayType = "any";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		
		ProbabilityDistribution durDist = new Gaussian(1, 1, true); 			
//		durDist.precompute(0, 1439, 1440);
		act1.addDuration(actmodDayType, durDist);
		
		ProbabilityDistribution startDist = new Histogram(DistributionsLibrary.getStartTimeHistForCleaning());
		act1.addStartTime(actmodDayType, startDist);
		
		double[] v4 = {0.25,0.375,0.25,0,0,0,0,0.125};
		ProbabilityDistribution timesDist = new Histogram(v4);
		act1.addTimes(actmodDayType, timesDist);
		
		boolean shiftable = false;
		act1.addShiftable(actmodDayType, shiftable);
		boolean exclusive = true;
		act1.addConfig(actmodDayType, exclusive);
		
		String[] containsAppliances = {"appl1", "appl3", "appl4", "appl5"};
//		String[] containsAppliances = {"appl1"};
		// add appliances
		act1.addAppliances(containsAppliances, existing, actmodDayType);
		
		person.addActivity(act1);
		
		
		activityName = "Lighting";
		activityID = "act2";
		activityDesc = "Person Lighting Activity";
		Activity.Builder actBuilder = new Activity.Builder(activityID, activityName, activityDesc, "", simParams);
		
		actmodDayType = "any";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		
		ProbabilityDistribution durDist2 = new Gaussian(1, 1, true); 				
//		durDist2.precompute(0, 1439, 1440);
		actBuilder.duration(actmodDayType, durDist2);
		
		ProbabilityDistribution startDist2 = new Histogram(DistributionsLibrary.getStartTimeHistForLighting());
		actBuilder.startTime(actmodDayType, startDist2);
		
		double[] v2a = {0.22222,0.33333,0.44444};
		ProbabilityDistribution timesDist2 = new Histogram(v2a);
		actBuilder.times(actmodDayType, timesDist2);
		
		shiftable = false;
		actBuilder.shiftable(actmodDayType, shiftable);
		
		Activity act2 = actBuilder.build();
		exclusive = true;
		act2.addConfig(actmodDayType, exclusive);
		
		String[] containsAppliances2 = {"appl2"};
		// add appliances
		act2.addAppliances(containsAppliances2, existing, actmodDayType);
		
		person.addActivity(act2);
		
		inst.addPerson(person);	
		installations.add(inst);
		
		
		//Create the installation
		instName = "Fani's house";			// installation names
		instID= "inst2";								// installation ids
		instDescription = "Fani's house";	// installation descriptions
		instType = "lala1";							// installation types
		Installation inst2 = new Installation.Builder(instID, instName, instDescription, instDescription, this.pricing, this.baseline_pricing).build();
		
		// Create the appliances
		existing = new HashMap<String,Appliance>();
		
		applName ="Cleaning Washing Machine";
		appliID = "appl21";
		applDescription = "Description of Cleaning Washing Machine";
		applType = "Washing";
		applStandByCons = 0;
		applIsBase = false;
		consModelsP = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("p");
		consModelsQ = ConsumptionModelsLibrary.getConsumptionModelForWashingMachine("q");
		Appliance app21 = new Appliance.Builder(appliID,  applName, applDescription, applType, 
				inst2, consModelsP, consModelsQ, applStandByCons, applIsBase).build(getOrng());
		existing.put(appliID, app21);
		inst2.addAppliance(app21);
		
		// Create the people
		personName = "Fani";
		personID = "person2";
		personDesc ="Person";	
		personType ="Girl";	
		awareness= 0.9;
		sensitivity=0.7;
		Person person2 = new Person.Builder(personID, personName, personDesc, personType, inst2, awareness, sensitivity).build();
		
		// Create the activities
		activityName = "Cleaning"; 
		activityID = "act21";
		activityDesc = "Person Cleaning Activity"; 
		activityType = "lala"; 
		Activity act21 = new Activity.Builder(activityID, activityName, activityDesc, activityType, simParams).build();
		
		String[] containsAppliances21 = {"appl21"};
		
		actmodDayType = "weekends";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 	
		double[] w = {0.7, 0.3};
     	double[] means = {480, 1200};
     	double[] stds = {40, 60};
		ProbabilityDistribution durDist3 = new GaussianMixtureModels(w.length, w, means, stds, true);
//		durDist3.precompute(0, 1439, 1440);
		act21.addDuration(actmodDayType, durDist3);
		ProbabilityDistribution startDist3 = new Histogram(DistributionsLibrary.getStartTimeHistForCleaning());
		act21.addStartTime(actmodDayType, startDist3);
		double[] v42 = {0.25,0.375,0.25,0,0,0,0,0.125};
		ProbabilityDistribution timesDist3 = new Histogram(v42);
		act21.addTimes(actmodDayType, timesDist3);
		act21.addShiftable(actmodDayType, shiftable);
		act21.addConfig(actmodDayType, exclusive);
		act21.addAppliances(containsAppliances21, existing, actmodDayType);
		
		actmodDayType = "weekdays";  //any | weekdays | weekends | working | nonworking | abbreviations of specific weekdays, i.e. [Mon, Tue, Sat] | specific days formated as 1/12, 31/10 
		double[] durDist4V = {100.0, 50.0, 200.0};
		ProbabilityDistribution durDist4 = new Histogram(durDist4V);
		act21.addDuration(actmodDayType, durDist4);
		ProbabilityDistribution startDist4 = null;
		double from = 100;
		double to = 400;
		startDist4 = new Uniform(from, to, true);	
		act21.addStartTime(actmodDayType, startDist4);
		double[] timesDist4V = {0.2, 0.3, 0.5, 0.4};
		ProbabilityDistribution timesDist4 = new Histogram(timesDist4V);
		act21.addTimes(actmodDayType, timesDist4);
		act21.addShiftable(actmodDayType, shiftable);
		act21.addConfig(actmodDayType, exclusive);
		act21.addAppliances(containsAppliances21, existing, actmodDayType);
		
		person2.addActivity(act21);
		
		inst2.addPerson(person2);	
		
		installations.add(inst2);
		
  		return installations;
	}
	
	public static void main(String[] args)
	{	
		String output_path = "./";
		int seed = 171181;
		boolean useDerby = false;
		boolean printKPIs = true;
		
		MyStandaloneSimulation sim = new MyStandaloneSimulation(output_path, "SimpleStatic"+System.currentTimeMillis(), seed, useDerby);
  		try {
			sim.setup(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
  		sim.runSimulation();
		if (printKPIs)
			sim.printKPIs();
	}
	

}