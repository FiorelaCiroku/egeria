/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.subjectarea.samples;

import org.apache.log4j.varia.NullAppender;
import org.odpi.openmetadata.accessservices.subjectarea.SubjectArea;
import org.odpi.openmetadata.accessservices.subjectarea.SubjectAreaCategory;
import org.odpi.openmetadata.accessservices.subjectarea.SubjectAreaGlossary;
import org.odpi.openmetadata.accessservices.subjectarea.client.SubjectAreaImpl;
import org.odpi.openmetadata.accessservices.subjectarea.ffdc.exceptions.*;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.category.Category;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.category.SubjectAreaDefinition;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.glossary.Glossary;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.nodesummary.CategorySummary;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.nodesummary.GlossarySummary;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.project.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  SubjectAreaDefinition Sample provides a client program that calls the Subject Area OMAS to create sample Subject Area Definitions.
 */
public class SubjectAreaDefinitionSample
{
    /*
     * Subject areas for Coco Pharmaceuticals sample.
     */
    private static final String ORGANIZATION = "Organization";
    private static final String   HOSPITAL = "Hospital";
    private static final String   SUPPLIER = "Supplier";
    private static final String PERSON = "Person";
    private static final String   PATIENT = "Patient";
    private static final String   CLINICIAN = "Clinician";
    private static final String   EMPLOYEE = "Employee";
    private static final String   COLLABORATOR = "Collaborator";
    private static final String CLINICAL = "Clinical";
    private static final String   SYMPTOM = "Symptom";
    private static final String   MEASUREMENT = "Measurement";
    private static final String   PRESCRIPTION = "Prescription";
    private static final String   OUTCOME = "Outcome";
    private static final String TREATMENT = "Treatment";
    private static final String   PRODUCT = "Product";
    private static final String   ORDER = "Order";
    private static final String   RECIPE = "Recipe";
    private static final String SERVICE_QUALITY = "Service Quality";
    private static final String   CONTRACT = "Contract";
    private static final String   STOCK = "Stock";
    private static final String   DISTRIBUTION = "Distribution";
    private static final String   INVOICE = "Invoice";

    private static final String GLOSSARY_NAME = "Coco Pharmaceuticals Subject Area Definitions";
    private static final String GLOSSARY_DESCRIPTION = "Coco Pharmaceuticals Core Subject Areas for Personalized Medicine.";
    private static final String SERVER_NAME  = "CocoServer1";
    /*
     * Erin Overview userid
     */
    private static final String USERID = "Erin";
    public static final String DEFAULT_URL = "http://localhost:8080";
    private String serverURLRoot;
    private String clientUserId;
    private SubjectArea subjectArea = null;
    private SubjectAreaGlossary subjectAreaGlossary =null;
    private SubjectAreaCategory subjectAreaCategory =null;

    /**
     * Constructor
     *
     * @param serverURLRoot server to connect to.
     * @param clientUserId user id to use to access metadata.
     */
    private SubjectAreaDefinitionSample(String serverURLRoot, String clientUserId)
    {
        this.serverURLRoot = serverURLRoot;
        this.clientUserId = clientUserId;
    }

    /**
     * Run the sample
     * @throws SubjectAreaCheckedExceptionBase error
     */
    private void run() throws SubjectAreaCheckedExceptionBase
    {
        SubjectArea subjectArea = new SubjectAreaImpl(SERVER_NAME,this.serverURLRoot);
        subjectAreaGlossary = subjectArea.getSubjectAreaGlossary();
        subjectAreaCategory = subjectArea.getSubjectAreaCategory();

        System.out.println("----------------------------");
        System.out.println("Creating the Coco Pharmaceutical Glossary for Subject Area Definitions : ");

        Glossary glossary = createGlossary(USERID,GLOSSARY_NAME,GLOSSARY_DESCRIPTION);
        /*
         * create Organisation subject areas
         */

        String glossaryGuid =  glossary.getSystemAttributes().getGUID();
        SubjectAreaDefinition organisation = createTopSubjectAreaDefinition(ORGANIZATION,glossaryGuid);
        SubjectAreaDefinition hospital= createChildSubjectAreaDefinition(HOSPITAL,organisation,glossaryGuid);
        SubjectAreaDefinition supplier= createChildSubjectAreaDefinition(SUPPLIER,organisation,glossaryGuid);
        /*
         * create Person subject areas
         */
        SubjectAreaDefinition person = createTopSubjectAreaDefinition(PERSON,glossaryGuid);
        SubjectAreaDefinition patient= createChildSubjectAreaDefinition(PATIENT,person,glossaryGuid);
        SubjectAreaDefinition clinician= createChildSubjectAreaDefinition(CLINICIAN,person,glossaryGuid);
        SubjectAreaDefinition employee= createChildSubjectAreaDefinition(EMPLOYEE,person,glossaryGuid);
        SubjectAreaDefinition collaborator= createChildSubjectAreaDefinition(COLLABORATOR,person,glossaryGuid);
        /*
         * create Clinical subject areas
         */
        SubjectAreaDefinition clinical = createTopSubjectAreaDefinition(CLINICAL,glossaryGuid);
        SubjectAreaDefinition symptom= createChildSubjectAreaDefinition(SYMPTOM,clinical,glossaryGuid);
        SubjectAreaDefinition measurement= createChildSubjectAreaDefinition(MEASUREMENT,clinical,glossaryGuid);
        SubjectAreaDefinition prescription= createChildSubjectAreaDefinition(PRESCRIPTION,clinical,glossaryGuid);
        SubjectAreaDefinition outcome= createChildSubjectAreaDefinition(OUTCOME,clinical,glossaryGuid);
        /*
         * create Treatment subject areas
         */
        SubjectAreaDefinition treatment = createTopSubjectAreaDefinition(TREATMENT,glossaryGuid);
        SubjectAreaDefinition product= createChildSubjectAreaDefinition(PRODUCT,treatment,glossaryGuid);
        SubjectAreaDefinition order= createChildSubjectAreaDefinition(ORDER,treatment,glossaryGuid);
        SubjectAreaDefinition recipe= createChildSubjectAreaDefinition(RECIPE,treatment,glossaryGuid);
        /*
         * create Service Quality subject areas
         */
        SubjectAreaDefinition serviceQuality = createTopSubjectAreaDefinition(SERVICE_QUALITY,glossaryGuid);
        SubjectAreaDefinition contract= createChildSubjectAreaDefinition(CONTRACT,serviceQuality,glossaryGuid);
        SubjectAreaDefinition stock= createChildSubjectAreaDefinition(STOCK,serviceQuality,glossaryGuid);
        SubjectAreaDefinition distribution= createChildSubjectAreaDefinition(DISTRIBUTION,serviceQuality,glossaryGuid);
        SubjectAreaDefinition invoice= createChildSubjectAreaDefinition(INVOICE,serviceQuality,glossaryGuid);
    }

    /**
     * Create a top level subject area definition - this means a Subject Area Definition that does not have a superCategory.
     * @param name name of the Subject Area Definition to create
     * @param glossaryGuid guid of the glossary to associate the Subject Area with.
     * @return SubjectAreaDefinition the created Subject Area Definition
     * @throws  SubjectAreaCheckedExceptionBase error
     */
    private SubjectAreaDefinition createTopSubjectAreaDefinition( String name, String glossaryGuid)  throws SubjectAreaCheckedExceptionBase
    {
        System.out.println("----------------------------");
        System.out.println("Creating a top level Subject Area Definition called " + name);
        SubjectAreaDefinition subjectAreaDefinition = new SubjectAreaDefinition();
        subjectAreaDefinition.setName(name);
        GlossarySummary glossarySummary = new GlossarySummary();
        glossarySummary.setGuid(glossaryGuid);
        subjectAreaDefinition.setGlossary(glossarySummary);
        return subjectAreaCategory.createSubjectAreaDefinition(SERVER_NAME,USERID,subjectAreaDefinition);
    }

    /**
     * Create a child Subject Area Definition - this means a Subject Area Definition that has a superCategory.
     * @param name name of the Subject Area Definition to create
     * @param parent parent Category
     * @param glossaryGuid guid of the glossary to associate the Subject Area with.
     * @return SubjectAreaDefinition the created Subject Area Definition
     * @throws SubjectAreaCheckedExceptionBase error
     */
    private SubjectAreaDefinition createChildSubjectAreaDefinition(String name, SubjectAreaDefinition parent, String glossaryGuid) throws SubjectAreaCheckedExceptionBase
    {
        SubjectAreaDefinition subjectAreaDefinition = new SubjectAreaDefinition();
        subjectAreaDefinition.setName(name);
        GlossarySummary glossarySummary = new GlossarySummary();
        glossarySummary.setGuid(glossaryGuid);
        subjectAreaDefinition.setGlossary(glossarySummary);
        CategorySummary parentCategorysummary = new CategorySummary();
        parentCategorysummary.setGuid(parent.getSystemAttributes().getGUID());
        subjectAreaDefinition.setParentCategory(parentCategorysummary);
        SubjectAreaDefinition newSubjectAreaDefinition = subjectAreaCategory.createSubjectAreaDefinition(SERVER_NAME,USERID, subjectAreaDefinition);
        if (newSubjectAreaDefinition != null)
        {
            System.out.println("Created Subject Area Definition " + newSubjectAreaDefinition.getName() + " with guid " + newSubjectAreaDefinition.getSystemAttributes().getGUID() + ", parent SubjectArea Definition is " + parent.getName());
        }
        return newSubjectAreaDefinition;
    }

    /**
     * Create a Glossary
     * @param userid userid
     * @param glossaryName name of the glossary to create
     * @param glossaryDescription description of the Glossary
     * @return created glossary
     * @throws SubjectAreaCheckedExceptionBase error
     */
    private Glossary createGlossary(String userid,String glossaryName,String glossaryDescription) throws SubjectAreaCheckedExceptionBase
    {
        Glossary glossary = new Glossary();
        glossary.setName(glossaryName);
        glossary.setDescription(glossaryDescription);
        return subjectAreaGlossary.createGlossary(SERVER_NAME,USERID,glossary);
    }

    /**
     * The main program takes the URL root for the server.
     *
     * @param args URL root for the server
     * @throws IOException IO Exception
     */
    public static void main(String[] args) throws IOException
    {
        String  serverURLRoot = SubjectAreaDefinitionSample.getUrl(args);
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
        System.out.println("===============================");
        System.out.println("Subject Area Definition Sample ");
        System.out.println("===============================");
        System.out.println("Running against server: " + serverURLRoot);
        System.out.println("Using userId: " + USERID);
        try
        {
            SubjectAreaDefinitionSample  sample = new SubjectAreaDefinitionSample(serverURLRoot, USERID);

            sample.run();
        }
        catch (Throwable  error)
        {
            System.out.println("Exception: " + error.getClass().getName() + " with message " + error.getMessage());
            System.exit(-1);
        }
    }
    /**
     * This method gets the url that the tests will use to issue calls to the server.
     * <p>
     * If arguments are supplied then the first parameter is used as a url.
     * <p>
     * If no url is supplied then prompt the user to enter a valid url, enter means to use the default url.
     *
     * @param args arguments supplied
     * @return the url to use on the calls to the server
     * @throws IOException IO exception occured while getting input from the user.
     */
    public static String getUrl(String args[]) throws IOException
    {
        String url = null;
        if (args.length > 0)
        {
            url = args[0];
        } else
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter a URL. Press enter to get the default (" + DEFAULT_URL + ".)):");
            url = br.readLine();
            if (url.equals(""))
            {
                url = DEFAULT_URL;
            }

        }
        return url;
    }

}
