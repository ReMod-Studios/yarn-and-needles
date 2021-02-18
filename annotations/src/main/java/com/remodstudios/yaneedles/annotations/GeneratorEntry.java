package com.remodstudios.yaneedles.annotations;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Util;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

interface GeneratorEntry {
    void writeToClinit(CodeBlock.Builder clinitBuilder);

    class Simple implements GeneratorEntry {
        protected final Types types;
        public final TypeMirror resGenType;

        public Simple(Types types, TypeMirror resGenType) {
            this.types = types;
            this.resGenType = resGenType;
        }

        @Override
        public void writeToClinit(CodeBlock.Builder clinitBuilder) {
            clinitBuilder.add("new $T()", resGenType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Simple simple = (Simple) o;
            return types.isSameType(resGenType, simple.resGenType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resGenType);
        }
    }

    class WithArgs implements GeneratorEntry {
        protected final Types types;
        public final TypeMirror resGenType;
        public final Map<String, String> args;

        public WithArgs(Types types, TypeMirror resGenType, String argString) {
            this.types = types;
            this.resGenType = resGenType;
            this.args = Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
                for (String pair : argString.split(",")) {
                    String[] kv = pair.split("=");
                    map.put(kv[0], kv[1]);
                }
            });

        }

        @Override
        public void writeToClinit(CodeBlock.Builder clinitBuilder) {
            clinitBuilder.add(
                "new $T($T.make(new $T(), map -> {$>\n",
                resGenType, Util.class,
                ParameterizedTypeName.get(Object2ObjectLinkedOpenHashMap.class, String.class, String.class)
            );
            args.forEach((k,v) ->
                clinitBuilder.addStatement("map.put($S, $S)", k, v)
            );
            clinitBuilder.add("$<}))");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WithArgs simple = (WithArgs) o;
            return types.isSameType(resGenType, simple.resGenType) && args.equals(simple.args);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resGenType);
        }
    }
}