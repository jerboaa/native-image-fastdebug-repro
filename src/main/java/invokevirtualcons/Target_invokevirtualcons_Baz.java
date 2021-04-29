/*
 * Copyright (c) 2021, Red Hat Inc. All rights reserved.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package invokevirtualcons;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Inject;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.TargetElement;

import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaField;

@TargetClass(value = Baz.class)
final class Target_invokevirtualcons_Baz {

    @Alias //
    Target_Foo foo;
	
    @Inject @RecomputeFieldValue(kind = Kind.Custom, declClass = MyNeedsReinitializationProvider.class) //
    volatile int needsReinitialization;

    @Alias @InjectAccessors(BazAccessors.class) //
    private byte[] byteArray;
    
    /* Replacement injected fields that store the state at run time. */
    @Inject @RecomputeFieldValue(kind = Kind.Reset)//
    byte[] injectedByteArray;
    	
    @Alias
    @TargetElement(name = TargetElement.CONSTRUCTOR_NAME)
    native void originalConstructor(Target_Foo foo, String dir);
	
}

@TargetClass(value = Foo.class)
final class Target_Foo {
}

final class MyNeedsReinitializationProvider implements RecomputeFieldValue.CustomFieldValueComputer {
	
    static final int STATUS_NEEDS_REINITIALIZATION = -1;
    static final int STATUS_IN_REINITIALIZATION = 1;
    /*
     * This constant must remain 0 so that objects allocated at run time do not go through
     * re-initialization.
     */
    static final int STATUS_REINITIALIZED = 0;

    @Override
    public Object compute(MetaAccessProvider metaAccess, ResolvedJavaField original, ResolvedJavaField annotated, Object receiver) {
    	return STATUS_NEEDS_REINITIALIZATION;
    }
}

class BazAccessors {
	/*
     * Get-accessors for the fields of Foo. Re-initialization of all fields happen on the
     * first access of any of the fields.
     */

    static byte[] getByteArray(Target_invokevirtualcons_Baz that) {
        if (that.needsReinitialization != MyNeedsReinitializationProvider.STATUS_REINITIALIZED) {
            reinitialize(that);
        }
        return that.injectedByteArray;
    }
    
    /*
     * Set-accessors for the fields of Foo. These methods are invoked by the original
     * constructor. Providing these set-accessors is less error prone than doing a copy-paste-modify
     * of the constructor to write to the injected fields directly.
     */

    static void setByteArray(Target_invokevirtualcons_Baz that, byte[] value) {
        that.injectedByteArray = value;
    }


    private static synchronized void reinitialize(Target_invokevirtualcons_Baz that) {
        if (that.needsReinitialization != MyNeedsReinitializationProvider.STATUS_NEEDS_REINITIALIZATION) {
            /* Field initialized is volatile, so double-checked locking is OK. */
        	System.out.println("initializing (short return)");
            return;
        }
        System.out.println("initializing");
        
        /*
         * The original constructor reads fields immediately after writing, so we need to make sure
         * that we do not enter this re-initialization code recursively.
         */
        that.needsReinitialization = MyNeedsReinitializationProvider.STATUS_IN_REINITIALIZATION;

        /*
         * We invoke the original constructor of Foo. This overwrites the byteArray field
         * with the same value it is already set to, so this is harmless. All other field writes are
         * redirected to the set-accessors of this class and write the injected fields.
         *
	 * This method invocation triggers the assertion in the debug JVM.
         */
        that.originalConstructor(that.foo, System.getProperty("user.dir"));

        /*
         * Now the object is completely re-initialized and can be used by any thread without
         * entering the synchronized slow path again.
         */
        that.needsReinitialization = MyNeedsReinitializationProvider.STATUS_REINITIALIZED;
    }

}
