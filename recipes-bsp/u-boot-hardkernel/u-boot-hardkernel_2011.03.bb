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

require recipes-bsp/u-boot/u-boot.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"

COMPATIBLE_MACHINE = "odroid-c1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

UBOOT_REPO_URI ?= "git://github.com/hardkernel/u-boot.git"
UBOOT_BRANCH ?= "odroidc-v2011.03"

SRCREV = "f631c80969b33b796d2d4c077428b4765393ed2b"

PV = "v2011.03+git${SRCPV}"

PROVIDES =+ "u-boot ${PN}-config"
PACKAGES =+ "u-boot-ini"

SRC_URI = " \
    ${UBOOT_REPO_URI};branch=${UBOOT_BRANCH} \
    file://0001-ucl-use-host-compiler-supplied-by-OE.patch \
    file://0003-use-lldiv-for-64-bit-division.patch \
    file://0001-compiler_gcc-do-not-redefine-__gnu_attributes.patch \
    file://0001-ARM-asm-io.h-use-static-inline.patch \
    file://0001-board.c-fix-inline-issue.patch \
    file://0001-compile-add-gcc5.patch \
    file://0001-main-fix-inline-issue.patch \
    file://0001-usb-use-define-not-func.patch \
"

SRC_URI_append_odroid-c1 = " \
    file://boot.ini \
"

SRC_URI_append = " ${@bb.utils.contains('TUNE_FEATURES','callconvention-hard',' file://0002-added-hardfp-support.patch ','',d)}"

EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC}"'

PARALLEL_MAKE = ""

do_compile_append () {
    # Move result to usual location
    mv ${B}/sd_fuse/${UBOOT_BINARY} ${B}
}

BL1_SUFFIX ?= "bin.hardkernel"
BL1_IMAGE ?= "bl1-${MACHINE}-${PV}-${PR}.${BL1_SUFFIX}"
BL1_BINARY ?= "bl1.${BL1_SUFFIX}"
BL1_SYMLINK ?= "bl1-${MACHINE}.${BL1_SUFFIX}"

FILES_u-boot-ini = "/boot/boot.ini"
CONFFILES_u-boot-ini = "/boot/boot.ini"

do_install_append () {
    install -d ${D}/boot/
    install ${WORKDIR}/boot.ini ${D}/boot/boot.ini
}

do_deploy_append () {
    install ${S}/sd_fuse/${BL1_BINARY} ${DEPLOYDIR}/${BL1_IMAGE}

    cd ${DEPLOYDIR}
    rm -f ${BL1_BINARY} ${BL1_SYMLINK}
    ln -sf ${BL1_IMAGE} ${BL1_SYMLINK}
    ln -sf ${BL1_IMAGE} ${BL1_BINARY}
}
