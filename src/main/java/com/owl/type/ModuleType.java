package com.owl.type;

import com.owl.module.*;

import com.owl.module.Module;
import lombok.Getter;

@Getter
public enum ModuleType {

    USER_GUI_MODULE(UserModule.class),
    KERNEL_MODULE(KernelModule.class),
    FILES_MODULE(FilesModule.class),
    APPLICATION_MODULE(ApplicationModule.class)

    ;

    private Class<? extends Module> clazz;

    ModuleType(Class<? extends Module> clazz) {
        this.clazz = clazz;
    }

}
