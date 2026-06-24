package io.iskopasi.splittunnel

import io.iskopasi.splittunnel.managers.AppManagerData

expect fun getRunningProcesses(): List<AppManagerData>
