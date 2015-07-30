/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package TeamCitySrvrmgr.server;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sbt-voroshilov-ss on 27.07.2015.
 */
public interface W32API extends StdCallLibrary, W32Errors {

  /** Standard options to use the unicode version of a w32 API. */
  Map UNICODE_OPTIONS = new HashMap() {
    {
      put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
      put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
    }
  };

  /** Standard options to use the ASCII/MBCS version of a w32 API. */
  Map ASCII_OPTIONS = new HashMap() {
    {
      put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
      put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
    }
  };

  Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;

  public class HANDLE extends PointerType {
    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
      Object o = super.fromNative(nativeValue, context);
      if (INVALID_HANDLE_VALUE.equals(o))
        return INVALID_HANDLE_VALUE;
      return o;
    }
  }

  /** Constant value representing an invalid HANDLE. */
  HANDLE INVALID_HANDLE_VALUE = new HANDLE() {
    { super.setPointer(Pointer.createConstant(-1)); }
    @Override
    public void setPointer(Pointer p) {
      throw new UnsupportedOperationException("Immutable reference");
    }
  };
}


interface W32Errors {

  int NO_ERROR = 0;
  int ERROR_INVALID_FUNCTION = 1;
  int ERROR_FILE_NOT_FOUND = 2;
  int ERROR_PATH_NOT_FOUND = 3;

}
