package io.sitoolkit.cv.plugin.maven;


import static org.junit.Assert.*;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RunApplicationMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable
        {
        }

        @Override
        protected void after()
        {
        }
    };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * @throws Exception if any
     */
    @Test()
    public void testSomething()
            throws Exception
    {
        expectedException.expect(MojoExecutionException.class);
        expectedException.expectMessage("SIT-CV-App start failed");

        File pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        RunApplicationMojo mojo = ( RunApplicationMojo ) rule.lookupConfiguredMojo( pom, "run" );
        assertNotNull( mojo );
        mojo.execute();

    }

    /** Do not need the MojoRule. */
    @WithoutMojo
    @Test
    public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn()
    {
        assertTrue( true );
    }

}

