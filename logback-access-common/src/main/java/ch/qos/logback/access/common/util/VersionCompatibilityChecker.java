package ch.qos.logback.access.common.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.EnvUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.util.Optional;
import java.util.Properties;

import static ch.qos.logback.core.CoreConstants.NA;

/**
 * A helper class for checking version compatibility between various Logback artifacts.
 * This class provides methods to retrieve and verify the versions of different
 * Logback-related components to ensure they match expected values.
 *
 * @since 2.0.9
 */
public class VersionCompatibilityChecker {
    static public String LOGBACK_ACCESS_COMMON_VERSION_MESSAGE = "Found logback-access-common version ";
    static String EXPECTED_VERSION_MESSAGE = "Was expecting logback-core version ";
    static String LOGBACK_CORE_VERSIONS_MISMATCH =  "Expected and actual versions of logback-core are different!";


    /**
     * Retrieves the version of Logback Access artifact, access-common, access-jetty11, access-tomcat11 etc.
     *
     * <p>The aClass parameter is assumed to be part of the artifact.
     * </p>
     *
     * <p>The method first attempts to get the version from the module information. If the module version
     * is not available, it falls back to retrieving the implementation version from the package.
     * </p>
     * @param aClass the class from which to retrieve the version information
     * @return the version of the artifact where aClass is found, or null if the version cannot be determined
     * @since 2.0.9
     */
    static public String getVersionOfArtifact(Class<?> aClass) {
        String moduleVersion = getVersionOfClassByModule(aClass);
        if (moduleVersion != null)
            return moduleVersion;

        Package pkg = aClass.getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getImplementationVersion();
    }


    /**
     * Retrieves the version of an artifact from the artifact's module metadata.
     *
     * <p>If the module or its descriptor does not provide a version, the method returns null.
     *</p>
     *
     *
     * @param aClass a class from which to retrieve the version information
     * @return the version of class' module as a string, or null if the version cannot be determined
     * @since 2.0.9
     */
    static private String getVersionOfClassByModule(Class<?> aClass) {
        Module module = aClass.getModule();
        if (module == null)
            return null;

        ModuleDescriptor md = module.getDescriptor();
        if (md == null)
            return null;
        Optional<String> opt = md.rawVersion();
        return opt.orElse(null);
    }

    static String getExpectedVersionOfLogbackCoreByProperties() {
        Properties props = new Properties();
        try (InputStream is = VersionCompatibilityChecker.class.getClassLoader()
                .getResourceAsStream("dependency-versions.properties")) {
            if (is != null) {
                props.load(is);
                return props.getProperty("logback-core");
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    static String nonNull(String input) {
        if(input == null) {
            return NA;
        } else {
            return input;
        }
    }

    /**
     * Checks compatibility between the actual version of Logback Core and the expected version
     * based on project properties, logging information and warnings as needed.
     *
     * The method retrieves the current version of Logback Core and compares it with the expected
     * version defined in the dependency properties. If there is a mismatch, informational and
     * warning messages are logged to the provided context's status manager.
     *
     * @param context the context in which the version compatibility check is performed
     */
    static public void checkLogbackCoreVersionCompatibility(Context context) {
        String versionOfLogbackAccessCommon = nonNull(getVersionOfArtifact(VersionCompatibilityChecker.class));

        String actualVersionOfLogbackCore = null;
        try {
            actualVersionOfLogbackCore = EnvUtil.logbackVersion();
        } catch (NoSuchMethodError e) {
            // the actual version of logback is an older version
        }

        if (actualVersionOfLogbackCore == null) {
            actualVersionOfLogbackCore = CoreConstants.NA;
        }
        context.getStatusManager().add(new InfoStatus("Found logback-core version " + actualVersionOfLogbackCore, context));
        String expectedVersionOfLogbackCore = getExpectedVersionOfLogbackCoreByProperties();
        context.getStatusManager().add(new InfoStatus(LOGBACK_ACCESS_COMMON_VERSION_MESSAGE + versionOfLogbackAccessCommon, context));

        if(versionOfLogbackAccessCommon.equals(NA) || !expectedVersionOfLogbackCore.equals(actualVersionOfLogbackCore)) {
            context.getStatusManager().add(new InfoStatus(EXPECTED_VERSION_MESSAGE + expectedVersionOfLogbackCore, context));
            context.getStatusManager().add(new WarnStatus(LOGBACK_CORE_VERSIONS_MISMATCH, context));
        }
    }

    /**
     * Checks the version compatibility between logback-access-common and a dependent module.
     * <p>Compares the version of logback-access-common with the version of the specified module.
     * If the versions are different, informational and warning messages are logged to the context's status manager.
     * </p>
     * @param context the context that tracks the status of the logging process
     * @param moduleName the name of the dependent module to compare version compatibility with
     *
     */
    static public void checkAccessCommonAndDependentModuleVersionEquality(Context context, String moduleName) {
        String versionOfLogbackAccessCommon = null;

        try {
            versionOfLogbackAccessCommon = getVersionOfArtifact(VersionCompatibilityChecker.class);
        } catch (NoClassDefFoundError e) {

        }

        if(versionOfLogbackAccessCommon == null) {
            versionOfLogbackAccessCommon  =  NA;
        }

        String versionOfLogbackAccessJetty11 = getVersionOfArtifact(context.getClass());
        if(versionOfLogbackAccessJetty11 == null) {
            versionOfLogbackAccessJetty11 = NA;
        }

        context.getStatusManager().add(new InfoStatus("Found "+moduleName+ " version " + versionOfLogbackAccessJetty11, context));
        if (NA.equals(versionOfLogbackAccessCommon) || !versionOfLogbackAccessJetty11.equals(versionOfLogbackAccessCommon)) {
            context.getStatusManager().add(new InfoStatus(LOGBACK_ACCESS_COMMON_VERSION_MESSAGE + versionOfLogbackAccessCommon, context));
            context.getStatusManager().add(new WarnStatus("Versions of logback-access and "+moduleName +" are different!", context));
        }
    }

}
