class p1 extends MethodVisitor {

@Override
public void visitInsn(int opcode) {
    switch (opcode) {
	case Opcodes.MONITORENTER:
	    mv.visitInsn(Opcodes.DUP);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logLock","(Ljava/lang/Object;)V",false);
	    break;
	case Opcodes.MONITOREXIT:
	    mv.visitInsn(Opcodes.DUP);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
	    break;
	case Opcodes.IRETURN:
	    mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.LRETURN:
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.FRETURN:
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.DRETURN:
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.ARETURN:
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.RETURN:
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
            break;
	case Opcodes.ATHROW:
	{
	    if(isSynchronized){
		if(isStatic){
		    mv.visitInsn(Opcodes.ACONST_NULL);
		    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
		}
		else{
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log", "logUnlock","(Ljava/lang/Object;)V",false);
		}
	    }
	}
	default:break;
    }
    mv.visitInsn(opcode);
}
}

