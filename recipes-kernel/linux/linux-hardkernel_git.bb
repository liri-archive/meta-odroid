#
# This file is part of Liri.
#
# Copyright (C) 2017 Pier Luigi Fiorini <pierluigi.fiorini@gmail.com>
#
# $BEGIN_LICENSE:GPL3+$
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# $END_LICENSE$
#

require recipes-kernel/linux/linux-dtb.inc
inherit kernel siteinfo

DESCRIPTION = "Linux kernel for the Hardkernel ODROID devices"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

DEPENDS = "lzop-native"

COMPATIBLE_MACHINE = "odroid-c1"

KBRANCH ?= "odroid-3.8.y"
KBRANCH_odroid-c1 ?= "odroidc-3.10.y"

SRC_URI = " \
    git://github.com/hardkernel/linux.git;branch=${KBRANCH} \
    file://defconfig \
"

SRCREV = "${AUTOREV}"
SRCREV_odroid-c1 = "3ee55a996a2aa9e31daf906b5bca918e2beb479d"

S = "${WORKDIR}/git"

KV = "3.8.13"
KV_odroid-c1 = "3.10.104"
PV = "${KV}+gitr${SRCPV}"
LOCALVERSION ?= "odroid"

PACKAGES =+ "kernel-dbg kernel-headers"

FILES_kernel-dbg =+ " \
    ${exec_prefix}/src/kernel/drivers/amlogic/*/.debug \
    ${exec_prefix}/src/kernel/drivers/amlogic/*/*/.debug \
    ${exec_prefix}/src/kernel/drivers/amlogic/*/*/*/.debug \
"

FILES_kernel-headers = "${exec_prefix}/src/linux*"

# Set a variable in .configure
# $1 - Configure variable to be set
# $2 - value [n/y/value]
kernel_configure_variable() {
    # Remove the config
    CONF_SED_SCRIPT="$CONF_SED_SCRIPT /CONFIG_$1[ =]/d;"
    if test "$2" = "n"
    then
        echo "# CONFIG_$1 is not set" >> ${B}/.config
    else
        echo "CONFIG_$1=$2" >> ${B}/.config
    fi
}
