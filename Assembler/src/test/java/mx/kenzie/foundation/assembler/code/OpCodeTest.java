package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.MethodBuilderTest;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static mx.kenzie.foundation.assembler.code.OpCode.BIPUSH;
import static mx.kenzie.foundation.assembler.code.OpCode.SIPUSH;

public class OpCodeTest extends MethodBuilderTest {

    @Test
    public void mnemonic() throws NoSuchFieldException, IllegalAccessException {
        for (OpCode code : Codes.getAllOpcodes()) {
            assert OpCode.class.isAssignableFrom(OpCode.class.getDeclaredField(code.mnemonic()).getType());
        }
        for (Field field : OpCode.class.getDeclaredFields()) {
            field.setAccessible(true);
            final OpCode code = (OpCode) field.get(null);
            final byte b = Codes.class.getDeclaredField(code.mnemonic()).getByte(null);
            assert b == code.code();
        }
    }

    @Test
    public void code() {
        assert OpCode.opcodes().length == 202;
        for (OpCode code : OpCode.opcodes()) {
            assert code != null;
            assert Byte.toUnsignedInt(code.code()) < 202;
        }
    }

    @Test
    public void length() {
        for (OpCode code : OpCode.opcodes()) {
            assert code != null;
            assert (code.hasFixedLength() && code.length() > 0) || code.length() == -1;
        }
    }

    @Test
    public void hasFixedLength() {
    }

    @Test
    public void raw() {
    }

    @Test
    public void testRaw() {
    }

    @Test
    public void testAALOAD() {
    }

    @Test
    public void testAASTORE() {
    }

    @Test
    public void testACONSTNULL() {
    }

    @Test
    public void testALOAD0() {
    }

    @Test
    public void testALOAD1() {
    }

    @Test
    public void testALOAD2() {
    }

    @Test
    public void testALOAD3() {
    }

    @Test
    public void testALOAD() {
    }

    @Test
    public void testANEWARRAY() {
    }

    @Test
    public void testARETURN() {
    }

    @Test
    public void testARRAYLENGTH() {
    }

    @Test
    public void testASTORE0() {
    }

    @Test
    public void testASTORE1() {
    }

    @Test
    public void testASTORE2() {
    }

    @Test
    public void testASTORE3() {
    }

    @Test
    public void testASTORE() {
    }

    @Test
    public void testATHROW() {
    }

    @Test
    public void testBALOAD() {
    }

    @Test
    public void testBASTORE() {
    }

    @Test
    public void testBIPUSH() {
        assert BIPUSH.length() == 2 && BIPUSH.hasFixedLength();
        for (int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
            assert BIPUSH.value(i).code() == Codes.BIPUSH;
            assert BIPUSH.value((byte) i).code() == Codes.BIPUSH;
        }
    }

    @Test
    public void testCALOAD() {
    }

    @Test
    public void testCASTORE() {
    }

    @Test
    public void testCHECKCAST() {
    }

    @Test
    public void testD2F() {
    }

    @Test
    public void testD2I() {
    }

    @Test
    public void testD2L() {
    }

    @Test
    public void testDADD() {
    }

    @Test
    public void testDALOAD() {
    }

    @Test
    public void testDASTORE() {
    }

    @Test
    public void testDCMPG() {
    }

    @Test
    public void testDCMPL() {
    }

    @Test
    public void testDCONST0() {
    }

    @Test
    public void testDCONST1() {
    }

    @Test
    public void testDDIV() {
    }

    @Test
    public void testDLOAD0() {
    }

    @Test
    public void testDLOAD1() {
    }

    @Test
    public void testDLOAD2() {
    }

    @Test
    public void testDLOAD3() {
    }

    @Test
    public void testDLOAD() {
    }

    @Test
    public void testDMUL() {
    }

    @Test
    public void testDNEG() {
    }

    @Test
    public void testDREM() {
    }

    @Test
    public void testDRETURN() {
    }

    @Test
    public void testDSTORE0() {
    }

    @Test
    public void testDSTORE1() {
    }

    @Test
    public void testDSTORE2() {
    }

    @Test
    public void testDSTORE3() {
    }

    @Test
    public void testDSTORE() {
    }

    @Test
    public void testDSUB() {
    }

    @Test
    public void testDUP() {
    }

    @Test
    public void testDUPX1() {
    }

    @Test
    public void testDUPX2() {
    }

    @Test
    public void testDUP2() {
    }

    @Test
    public void testDUP2X1() {
    }

    @Test
    public void testDUP2X2() {
    }

    @Test
    public void testF2D() {
    }

    @Test
    public void testF2I() {
    }

    @Test
    public void testF2L() {
    }

    @Test
    public void testFADD() {
    }

    @Test
    public void testFALOAD() {
    }

    @Test
    public void testFASTORE() {
    }

    @Test
    public void testFCMPG() {
    }

    @Test
    public void testFCMPL() {
    }

    @Test
    public void testFCONST0() {
    }

    @Test
    public void testFCONST1() {
    }

    @Test
    public void testFCONST2() {
    }

    @Test
    public void testFDIV() {
    }

    @Test
    public void testFLOAD0() {
    }

    @Test
    public void testFLOAD1() {
    }

    @Test
    public void testFLOAD2() {
    }

    @Test
    public void testFLOAD3() {
    }

    @Test
    public void testFLOAD() {
    }

    @Test
    public void testFMUL() {
    }

    @Test
    public void testFNEG() {
    }

    @Test
    public void testFREM() {
    }

    @Test
    public void testFRETURN() {
    }

    @Test
    public void testFSTORE0() {
    }

    @Test
    public void testFSTORE1() {
    }

    @Test
    public void testFSTORE2() {
    }

    @Test
    public void testFSTORE3() {
    }

    @Test
    public void testFSTORE() {
    }

    @Test
    public void testFSUB() {
    }

    @Test
    public void testGETFIELD() {
    }

    @Test
    public void testGETSTATIC() {
    }

    @Test
    public void testGOTO() {
    }

    @Test
    public void testGOTOW() {
    }

    @Test
    public void testI2B() {
    }

    @Test
    public void testI2C() {
    }

    @Test
    public void testI2D() {
    }

    @Test
    public void testI2F() {
    }

    @Test
    public void testI2L() {
    }

    @Test
    public void testI2S() {
    }

    @Test
    public void testIADD() {
    }

    @Test
    public void testIALOAD() {
    }

    @Test
    public void testIAND() {
    }

    @Test
    public void testIASTORE() {
    }

    @Test
    public void testICONSTM1() {
    }

    @Test
    public void testICONST0() {
    }

    @Test
    public void testICONST1() {
    }

    @Test
    public void testICONST2() {
    }

    @Test
    public void testICONST3() {
    }

    @Test
    public void testICONST4() {
    }

    @Test
    public void testICONST5() {
    }

    @Test
    public void testIDIV() {
    }

    @Test
    public void testIFACMPEQ() {
    }

    @Test
    public void testIFACMPNE() {
    }

    @Test
    public void testIFICMPEQ() {
    }

    @Test
    public void testIFICMPNE() {
    }

    @Test
    public void testIFICMPLT() {
    }

    @Test
    public void testIFICMPGE() {
    }

    @Test
    public void testIFICMPGT() {
    }

    @Test
    public void testIFICMPLE() {
    }

    @Test
    public void testIFEQ() {
    }

    @Test
    public void testIFNE() {
    }

    @Test
    public void testIFLT() {
    }

    @Test
    public void testIFGE() {
    }

    @Test
    public void testIFGT() {
    }

    @Test
    public void testIFLE() {
    }

    @Test
    public void testIFNONNULL() {
    }

    @Test
    public void testIFNULL() {
    }

    @Test
    public void testIINC() {
    }

    @Test
    public void testILOAD0() {
    }

    @Test
    public void testILOAD1() {
    }

    @Test
    public void testILOAD2() {
    }

    @Test
    public void testILOAD3() {
    }

    @Test
    public void testILOAD() {
    }

    @Test
    public void testIMUL() {
    }

    @Test
    public void testINEG() {
    }

    @Test
    public void testINSTANCEOF() {
    }

    @Test
    public void testINVOKEDYNAMIC() {
    }

    @Test
    public void testINVOKEINTERFACE() {
    }

    @Test
    public void testINVOKESPECIAL() {
    }

    @Test
    public void testINVOKESTATIC() {
    }

    @Test
    public void testINVOKEVIRTUAL() {
    }

    @Test
    public void testIOR() {
    }

    @Test
    public void testIREM() {
    }

    @Test
    public void testIRETURN() {
    }

    @Test
    public void testISHL() {
    }

    @Test
    public void testISHR() {
    }

    @Test
    public void testISTORE0() {
    }

    @Test
    public void testISTORE1() {
    }

    @Test
    public void testISTORE2() {
    }

    @Test
    public void testISTORE3() {
    }

    @Test
    public void testISTORE() {
    }

    @Test
    public void testISUB() {
    }

    @Test
    public void testIUSHR() {
    }

    @Test
    public void testIXOR() {
    }

    @Test
    public void testJSR() {
    }

    @Test
    public void testJSRW() {
    }

    @Test
    public void testL2D() {
    }

    @Test
    public void testL2F() {
    }

    @Test
    public void testL2I() {
    }

    @Test
    public void testLADD() {
    }

    @Test
    public void testLALOAD() {
    }

    @Test
    public void testLAND() {
    }

    @Test
    public void testLASTORE() {
    }

    @Test
    public void testLCMP() {
    }

    @Test
    public void testLCONST0() {
    }

    @Test
    public void testLCONST1() {
    }

    @Test
    public void testLDC() {
    }

    @Test
    public void testLDCW() {
    }

    @Test
    public void testLDC2W() {
    }

    @Test
    public void testLDIV() {
    }

    @Test
    public void testLLOAD0() {
    }

    @Test
    public void testLLOAD1() {
    }

    @Test
    public void testLLOAD2() {
    }

    @Test
    public void testLLOAD3() {
    }

    @Test
    public void testLLOAD() {
    }

    @Test
    public void testLMUL() {
    }

    @Test
    public void testLNEG() {
    }

    @Test
    public void testLOOKUPSWITCH() {
    }

    @Test
    public void testLOR() {
    }

    @Test
    public void testLREM() {
    }

    @Test
    public void testLRETURN() {
    }

    @Test
    public void testLSHL() {
    }

    @Test
    public void testLSHR() {
    }

    @Test
    public void testLSTORE0() {
    }

    @Test
    public void testLSTORE1() {
    }

    @Test
    public void testLSTORE2() {
    }

    @Test
    public void testLSTORE3() {
    }

    @Test
    public void testLSTORE() {
    }

    @Test
    public void testLSUB() {
    }

    @Test
    public void testLUSHR() {
    }

    @Test
    public void testLXOR() {
    }

    @Test
    public void testMONITORENTER() {
    }

    @Test
    public void testMONITOREXIT() {
    }

    @Test
    public void testMULTIANEWARRAY() {
    }

    @Test
    public void testNEW() {
    }

    @Test
    public void testNEWARRAY() {
    }

    @Test
    public void testNOP() {
    }

    @Test
    public void testPOP() {
    }

    @Test
    public void testPOP2() {
    }

    @Test
    public void testPUTFIELD() {
    }

    @Test
    public void testPUTSTATIC() {
    }

    @Test
    public void testRET() {
    }

    @Test
    public void testRETURN() {
    }

    @Test
    public void testSALOAD() {
    }

    @Test
    public void testSASTORE() {
    }

    @Test
    public void testSIPUSH() {
        for (int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++) {
            final short value = (short) i;
            final ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.put(0, new byte[] {(byte) (value >> 8), (byte) (value)});
            final short shr = buffer.getShort(0);
            assert shr == value : shr + " != " + value;
            buffer.put(0, new byte[] {(byte) (value >>> 8), (byte) (value)});
            final short ushr = buffer.getShort(0);
            assert ushr == shr : shr + " != " + ushr;
            assert SIPUSH.value(value).code() == Codes.SIPUSH;
        }
    }

    @Test
    public void testSWAP() {
    }

    @Test
    public void testTABLESWITCH() {
    }

    @Test
    public void testWIDE() {
    }

}