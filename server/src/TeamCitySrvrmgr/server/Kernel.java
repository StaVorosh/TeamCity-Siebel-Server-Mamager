package TeamCitySrvrmgr.server;

import com.sun.jna.Native;

public interface Kernel extends W32API {
  Kernel INSTANCE = (Kernel) Native.loadLibrary("kernel32", Kernel.class, DEFAULT_OPTIONS);

  HANDLE GetCurrentProcess();

  int GetProcessId(HANDLE Process);
}
