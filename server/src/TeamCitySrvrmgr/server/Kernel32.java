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

/**
 * Created by sbt-voroshilov-ss on 27.07.2015.
 */
import com.sun.jna.FromNativeContext;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.HashMap;
import java.util.Map;

public interface Kernel32 extends W32API {
  Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, DEFAULT_OPTIONS);
  /* http://msdn.microsoft.com/en-us/library/ms683179(VS.85).aspx */
  HANDLE GetCurrentProcess();
  /* http://msdn.microsoft.com/en-us/library/ms683215.aspx */
  int GetProcessId(HANDLE Process);
}