/*
 * jPSXdec: PlayStation 1 Media Decoder/Converter in Java
 * Copyright (C) 2007-2019  Michael Sabin
 * All rights reserved.
 *
 * Redistribution and use of the jPSXdec code or any derivative works are
 * permitted provided that the following conditions are met:
 *
 *  * Redistributions may not be sold, nor may they be used in commercial
 *    or revenue-generating business activities.
 *
 *  * Redistributions that are modified from the original source must
 *    include the complete source code, including the source code for all
 *    components used by a binary built from the modified sources. However, as
 *    a special exception, the source code distributed need not include
 *    anything that is normally distributed (in either source or binary form)
 *    with the major components (compiler, kernel, and so on) of the operating
 *    system on which the executable runs, unless that component itself
 *    accompanies the executable.
 *
 *  * Redistributions must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package legend.game.fmv;

import javax.annotation.Nonnull;

/**
 * Represents a 16-bit code readable by the PlayStation MDEC chip.
 * If the MDEC code is the first of a block, the top 6 bits indicate
 * the block's quantization scale, and the bottom 10 bits indicate
 * the "direct current" (DC) coefficient.
 * If the MDEC code is not the first of a block, and it is
 * not a {@link #MDEC_END_OF_DATA} code (0xFE00), then the top 6 bits indicate
 * the number of zeros preceeding an "alternating current" (AC) coefficient,
 * with the bottom 10 bits indicating a (usually) non-zero AC coefficient.
 */
public class MdecCode implements Comparable<MdecCode> {
  /**
   * 16-bit MDEC code indicating the end of a block.
   * The equivalent MDEC value is (63, -512).
   */
  public final static int MDEC_END_OF_DATA = 0xFE00;
  /**
   * Top 6 bits of {@link #MDEC_END_OF_DATA}.
   */
  public final static int MDEC_END_OF_DATA_TOP6 = (MDEC_END_OF_DATA >> 10) & 63;
  /**
   * Bottom 10 bits of {@link #MDEC_END_OF_DATA}.
   */
  public final static int MDEC_END_OF_DATA_BOTTOM10 = (short)(MDEC_END_OF_DATA | 0xFC00);

  /**
   * Most significant 6 bits of the 16-bit MDEC code.
   * Holds either a block's quantization scale or the
   * count of zero AC coefficients leading up to a non-zero
   * AC coefficient.
   */
  private int _iTop6Bits;

  /**
   * Least significant 10 bits of the 16-bit MDEC code.
   * Holds either the DC coefficient of a block or
   * a non-zero AC coefficient.
   */
  private int _iBottom10Bits;

  public MdecCode(final int iTop6Bits, final int iBottom10Bits) {
    if(!validTop(iTop6Bits)) {
      throw new IllegalArgumentException("Invalid top 6 bits " + iTop6Bits);
    }
    if(!validBottom(iBottom10Bits)) {
      throw new IllegalArgumentException("Invalid bottom 10 bits " + iBottom10Bits);
    }
    this._iTop6Bits = iTop6Bits;
    this._iBottom10Bits = iBottom10Bits;
  }

  public void setFrom(@Nonnull final MdecCode other) {
    this._iTop6Bits = other._iTop6Bits;
    this._iBottom10Bits = other._iBottom10Bits;
  }

  public int getBottom10Bits() {
    return this._iBottom10Bits;
  }

  public int getTop6Bits() {
    return this._iTop6Bits;
  }

  public void set(final int iMdecWord) {
    this._iTop6Bits = ((iMdecWord >> 10) & 63);
    this._iBottom10Bits = (iMdecWord & 0x3FF);
    if((this._iBottom10Bits & 0x200) == 0x200) { // is it negitive?
      this._iBottom10Bits -= 0x400;
    }
  }

  /**
   * Combines the top 6 bits and bottom 10 bits into an unsigned 16 bit value.
   */
  public int toMdecWord() {
    if(this.isEOD()) {
      return MDEC_END_OF_DATA;
    }
    if(!validTop(this._iTop6Bits)) {
      throw new IllegalStateException("MDEC code has invalid top 6 bits " + this._iTop6Bits);
    }
    if(!validBottom(this._iBottom10Bits)) {
      throw new IllegalStateException("MDEC code has invalid bottom 10 bits " + this._iBottom10Bits);
    }
    return ((this._iTop6Bits & 63) << 10) | (this._iBottom10Bits & 0x3FF);
  }

  /**
   * Returns if this MDEC code is setFrom to the special "End of Data" (EOD)
   * value.
   */
  public boolean isEOD() {
    return (this._iTop6Bits == MDEC_END_OF_DATA_TOP6 &&
      this._iBottom10Bits == MDEC_END_OF_DATA_BOTTOM10);
  }

  /**
   * Returns if this MDEC code has valid values.
   * As an optimization, many parameter checks are disabled, so
   * this MDEC code could hold values that are be invalid.
   */
  public boolean isValid() {
    return validTop(this._iTop6Bits) && validBottom(this._iBottom10Bits);
  }

  /**
   * Checks if the top 6 bits of an MDEC code are valid.
   */
  private static boolean validTop(final int iTop6Bits) {
    return iTop6Bits >= 0 && iTop6Bits <= 63;
  }

  /**
   * Checks if the bottom 10 bits of an MDEC code are valid.
   */
  private static boolean validBottom(final int iBottom10Bits) {
    return iBottom10Bits >= -512 && iBottom10Bits <= 511;
  }

  public @Nonnull MdecCode copy() {
    return new MdecCode(this._iTop6Bits, this._iBottom10Bits);
  }

  @Override
  public String toString() {
    final String s = String.format("%04x (%d, %d)", this.toMdecWord(), this._iTop6Bits, this._iBottom10Bits);
    if(this.isEOD()) {
      return s + " EOD";
    } else {
      return s;
    }
  }

  public int compareTo(final MdecCode o) {
    final int i = Integer.compare(this._iTop6Bits, o._iTop6Bits);
    if(i != 0) {
      return i;
    }
    return Integer.compare(this._iBottom10Bits, o._iBottom10Bits);
  }

  @Override
  public boolean equals(final Object obj) {
    if(obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    final MdecCode other = (MdecCode)obj;
    return this._iTop6Bits == other._iTop6Bits &&
      this._iBottom10Bits == other._iBottom10Bits;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + this._iTop6Bits;
    hash = 97 * hash + this._iBottom10Bits;
    return hash;
  }

}
