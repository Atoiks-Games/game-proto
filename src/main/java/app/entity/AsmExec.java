/**
 * MIT License
 *
 * Copyright (c) 2017 Paul T.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.entity;

import java.awt.Point;

import java.util.List;
import java.util.ArrayList;

public class AsmExec {

    public static final byte OP_HLT = 0;
    public static final byte OP_LDR = 1;
    public static final byte OP_LDN = 2;
    public static final byte OP_LDB = 3;
    public static final byte OP_LDW = 4;
    public static final byte OP_LDA = 5;
    public static final byte OP_STA = 6;
    public static final byte OP_ADD = 7;
    public static final byte OP_SUB = 8;
    public static final byte OP_MUL = 9;
    public static final byte OP_DIV = 10;
    public static final byte OP_MOD = 11;
    public static final byte OP_AND = 12;
    public static final byte OP_OR = 13;
    public static final byte OP_XOR = 14;
    public static final byte OP_NOT_NEG = 15;
    public static final byte OP_SHL = 16;
    public static final byte OP_SHR = 17;
    public static final byte OP_SHS = 18;
    public static final byte OP_IN_OUT = 19;
    public static final byte OP_CMP = 20;
    public static final byte OP_JMP = 21;
    public static final byte OP_JE = 22;
    public static final byte OP_JN = 23;
    public static final byte OP_JL = 24;
    public static final byte OP_JG = 25;
    public static final byte OP_JLE = 26;
    public static final byte OP_JGE = 27;
    public static final byte OP_JBE = 28;
    public static final byte OP_INC_DEC = 29;

    private int[] regs = {
	0, 0, 0, 0,		// r0 (A), r1 (B), r2 (C), r3 (D)
	0, 0, 0, 0,		// r4 (I), r5 (J), r6 (K), r7 (L)
	0, 0, 0, 0,		// r8 (W), r9 (X), r10 (Y), r11 (Z)
	0, 0, 0, 0		// r12 (zero), r13 (flags), r14 (dp), r15 (ip)
    };

    private byte[] mem;

    public AsmExec (byte[] mem) {
	this.mem = mem;
    }

    public int[] execute (int[] input) {
	final List<Integer> output = new ArrayList<>();

	outer:
	for (regs[15] = 0; regs[15] < mem.length; ++regs[15]) {
	    regs[12] = 0;
	    switch (mem[regs[15]]) {
	    case OP_HLT:	// (Halt)
		break outer;
	    case OP_LDR: {	// (Load register value)
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] = regs[op & 0xf];
		break;
	    }
	    case OP_LDN: {	// (Load 4 bit)
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] = op & 0xf;
		break;
	    }
	    case OP_LDB: {	// (Load 8 bit)
		final int op = mem[++regs[15]];
		regs[op] = mem[++regs[15]];
		break;
	    }
	    case OP_LDW: {	// (Load 16 bit)
		final int op = mem[++regs[15]];
		final int hi = mem[++regs[15]];
		final int lo = mem[++regs[15]];
		regs[op] = (hi << 8) | lo;
		break;
	    }
	    case OP_LDA: {	// (Load address)
		final int op = mem[++regs[15]];
		final int hi = mem[++regs[15]];
		final int lo = mem[++regs[15]];
		regs[op] = mem[(hi << 8) | lo];
		break;
	    }
	    case OP_STA: {	// (Store address)
		final int op = mem[++regs[15]];
		final int hi = mem[++regs[15]];
		final int lo = mem[++regs[15]];
		mem[(hi << 8) | lo] = (byte) regs[op];
		break;
	    }
	    case OP_ADD: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] += regs[op & 0xf];
		break;
	    }
	    case OP_SUB: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] -= regs[op & 0xf];
		break;
	    }
	    case OP_MUL: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] *= regs[op & 0xf];
		break;
	    }
	    case OP_DIV: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] /= regs[op & 0xf];
		break;
	    }
	    case OP_MOD: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] %= regs[op & 0xf];
		break;
	    }
	    case OP_AND: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] &= regs[op & 0xf];
		break;
	    }
	    case OP_OR: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] |= regs[op & 0xf];
		break;
	    }
	    case OP_XOR: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] ^= regs[op & 0xf];
		break;
	    }
	    case OP_NOT_NEG: {
		final int op = mem[++regs[15]];
		if ((op & 0xf) == 0) // not
		    regs[(op & 0xf0) >> 4] = ~regs[(op & 0xf0) >> 4];
		else		// negate
		    regs[(op & 0xf0) >> 4] = -regs[(op & 0xf0) >> 4];
		break;
	    }
	    case OP_SHR: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] <<= regs[op & 0xf];
		break;
	    }
	    case OP_SHL: {
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] >>= regs[op & 0xf];
		break;
	    }
	    case OP_SHS: {	// (signed right shift; see add)
		final int op = mem[++regs[15]];
		regs[(op & 0xf0) >> 4] >>>= regs[op & 0xf];
		break;
	    }
	    case OP_IN_OUT: {	// (Read/Write from input[])
		final int op = mem[++regs[15]];
		if ((op & 0xf) == 0) // out
		    output.add(regs[(op & 0xf0) >> 4]);
		else		// in
		    regs[(op & 0xf0) >> 4] = input[regs[14]++];
		break;
	    }
	    case OP_CMP: {	// (Compare)
		final int op = mem[++regs[15]];
		final int lhs = (op & 0xf0) >> 4;
		final int rhs = op & 0xf;
		if (lhs == rhs)
		    regs[13] |= 1;
		else
		    regs[13] &= ~1;
		if (lhs < rhs)
		    regs[13] |= 1 << 1;
		else
		    regs[13] &= ~(1 << 1);
		if (lhs > rhs)
		    regs[13] |= 1 << 2;
		else
		    regs[13] &= ~(1 << 2);
		break;
	    }
	    case OP_JMP: {	// (Jump to address)
		// Subtract one so it balances out the ++regs[15] in for-loop
		regs[15] = decodeInt () - 1;
	        break;
	    }
	    case OP_JE: {	// (Jump if equals)
		final int addr = decodeInt () - 1;
		if ((regs[13] & 1) == 1)
		    regs[15] = addr;
		break;
	    }
	    case OP_JN: {	// (Jump if not equals)
		final int addr = decodeInt () - 1;
		if ((regs[13] & 1) == 0)
		    regs[15] = addr;
		break;
	    }
	    case OP_JL: {	// (Jump if less)
		final int addr = decodeInt () - 1;
		if (((regs[13] >> 1) & 1) == 1)
		    regs[15] = addr;
		break;
	    }
	    case OP_JG: {	// (Jump if greater)
		final int addr = decodeInt () - 1;
		if (((regs[13] >> 2) & 1) == 1)
		    regs[15] = addr;
		break;
	    }
	    case OP_JLE: {	// (Jump if less or equals)
		final int addr = decodeInt () - 1;
		if ((regs[13] & 1) == 1 || ((regs[13] >> 1) & 1) == 1)
		    regs[15] = addr;
		break;
	    }
	    case OP_JGE: {	// (Jump if greater or equals)
		final int addr = decodeInt () - 1;
		if ((regs[13] & 1) == 1 || ((regs[13] >> 2) & 1) == 1)
		    regs[15] = addr;
		break;
	    }
	    case OP_JBE: {	// (Jump if input[] is empty)
		final int addr = decodeInt () - 1;
		if (regs[14] >= input.length)
		    regs[15] = addr;
		break;
	    }
	    case OP_INC_DEC: {	// (++/--)
		final int op = mem[++regs[15]];
		if ((op & 0xf) == 0) // dec
		    --regs[(op & 0xf0) >> 4];
		else		// inc
		    ++regs[(op & 0xf0) >> 4];
		break;
	    }
	    default:
		throw new RuntimeException ("Illegal opcode(" + mem[regs[15]] + ") found!");
	    }
	}
	final int[] rst = new int[output.size()];
	for (int i = 0; i < rst.length; ++i) {
	    rst[i] = output.get(i);
	}
	return rst;
    }

    private int decodeInt () {
	final int hh = mem[++regs[15]];
	final int lh = mem[++regs[15]];
	final int hl = mem[++regs[15]];
	final int ll = mem[++regs[15]];
	return (hh << 24) | (lh << 16) | (hl << 8) | ll;
    }
}
