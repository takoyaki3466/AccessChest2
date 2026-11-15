package com.takoy3466.accesschest.core.platform;

import java.nio.file.Path;

/**
 * Information about the running loader environment.
 */
public interface IPlatformInfo {

    /**
     * @return the directory where configuration files are stored
     */
    Path configDirectory();
}
