package com.github.robobario.shade;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.shade.DefaultShader;
import org.apache.maven.plugins.shade.ShadeRequest;
import org.apache.maven.plugins.shade.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("enforceJarName")
@Singleton
public class EnforceJarNameShader extends DefaultShader {

    private final EnforceJarNameFilter enforceJarNameFilter;

    public EnforceJarNameShader(){
        String expectJarNameContains = System.getProperty("enforceShadedJarNameContains");
        if (expectJarNameContains == null) {
            throw new RuntimeException("Requires system property enforceShadedJarNameContains");
        }
        boolean failOnViolation = Boolean.getBoolean("enforceShadedJarNameFailOnViolation");
        enforceJarNameFilter = new EnforceJarNameFilter(expectJarNameContains, failOnViolation);
    }

    @Override
    public void shade(ShadeRequest shadeRequest) throws IOException, MojoExecutionException {
        /*
          We rely on the enforcer being the final filter, the implication being we are only checking jars that have not been filtered
          by any user configuration.
         */
        List<Filter> filters = Stream.concat(shadeRequest.getFilters().stream(), Stream.of(enforceJarNameFilter)).collect(Collectors.toList());
        shadeRequest.setFilters(filters);
        super.shade(shadeRequest);
    }

    static class EnforceJarNameFilter implements Filter {
        final String expectJarNameContains;
        final boolean failOnViolation;
        private static final Logger LOGGER = LoggerFactory.getLogger(EnforceJarNameFilter.class);

        EnforceJarNameFilter(String expectJarNameContains, boolean failOnViolation){
            this.expectJarNameContains = expectJarNameContains;
            this.failOnViolation = failOnViolation;
        }

        @Override
        public boolean canFilter(File jar) {
            if (!jar.getName().contains(expectJarNameContains)) {
                String message = "jar name " + jar.getName() + " does not contain " + expectJarNameContains;
                LOGGER.warn(message);
                if(failOnViolation){
                    throw new RuntimeException(message);
                }
            }
            return false;
        }

        @Override
        public boolean isFiltered(String classFile) {
            // no action to take on classes
            return false;
        }

        @Override
        public void finished() {
            //noop
        }
    }
}