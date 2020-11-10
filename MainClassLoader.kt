package xyz.icodes.ichat

import android.content.Context
import android.util.Log
import java.lang.reflect.Field

/**
 * @author xechoz
 * replace context.classloader's parent ClassLoader to MainClassLoader
 * and then intercept loadClass or findClass methods
 */
class MainClassLoader(parent: ClassLoader) : ClassLoader(parent) {
    companion object {
        private const val TAG = "MainClassLoader"

        /**
         * call this on your custom Application
         *
         *  override fun attachBaseContext(base: Context) {
         *      super.attachBaseContext(base)
         *      MainClassLoader.interceptClassLoader(base)
         *  }
         */
        fun interceptClassLoader(context: Context) {
            // replace parent to our custom class loader
            // and we can intercept and then delegate method call back to parent class loader
            val custom = MainClassLoader(parent = context.classLoader.parent)

            val field = findField(context.classLoader, "parent")
            field?.set(context.classLoader, custom)
            Log.d(TAG, "interceptClassLoader $field")
        }

        /**
         * code copy from MultiDex
         */
        @Throws(NoSuchFieldException::class)
        private fun findField(instance: Any, name: String): Field? {
            var clazz: Class<*>? = instance.javaClass
            while (clazz != null) {
                try {
                    val field = clazz.getDeclaredField(name)
                    if (!field.isAccessible) {
                        field.isAccessible = true
                    }
                    return field
                } catch (e: NoSuchFieldException) {
                    // ignore and search next
                }
                clazz = clazz.superclass
            }
            throw NoSuchFieldException("Field " + name + " not found in " + instance.javaClass)
        }
    }

    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        Log.d(TAG, "loadClass $name, $resolve")
        return super.loadClass(name, resolve)
    }

    override fun findClass(name: String?): Class<*> {
        Log.d(TAG, "findClass $name")
        return super.findClass(name)
    }
}