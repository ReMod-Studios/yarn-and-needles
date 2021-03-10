package com.remodstudios.yaneedles.registry.impl;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

class AddRegistryHandleVisitor extends ClassVisitor {
    public AddRegistryHandleVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC | ACC_STATIC, "register", "()V", null, null);

        super.visitEnd();
    }
}