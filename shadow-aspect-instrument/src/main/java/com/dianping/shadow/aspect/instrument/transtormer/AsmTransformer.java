package com.dianping.shadow.aspect.instrument.transtormer;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by jourrey on 17/2/23.
 */
public class AsmTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassNode cn = new ClassNode();
        cr.accept(new MyClassVisivator(cn), 0);
//        for (Object obj : cn.methods) {
//            MethodNode md = (MethodNode) obj;
//            if ("<init>".endsWith(md.name) || "<clinit>".equals(md.name)) {
//                continue;
//            }
//            InsnList insns = md.instructions;
//            InsnList il = new InsnList();
//            il.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System",
//                    "out", "Ljava/io/PrintStream;"));
//            il.add(new LdcInsnNode("Enter method-> " + cn.name+"."+md.name));
//            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
//                    "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
//            insns.insert(il);
//            md.maxStack += 3;
//
//        }
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static class MyClassVisivator extends ClassAdapter {

        public MyClassVisivator(ClassVisitor classVisitor) {
            super(classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            System.out.println(signature);
            if ("main".equals(name)) {
                MyMethodVisitor mmv = new MyMethodVisitor(mv);
                return mmv;
            }
            return mv;
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "test03", "()V", null, null);
            if (mv != null) {
                mv.visitCode();
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("Hello In Test03!");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        }
    }

    static class MyMethodVisitor extends MethodAdapter {

        public MyMethodVisitor(MethodVisitor methodVisitor) {
            super(methodVisitor);
        }

        @Override
        public void visitCode() {
            System.out.println("Debug");
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                System.out.println("Debug");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dianping/shadow/silhouette/SilhouetteDefinition", "test03", "()V");
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }

}
