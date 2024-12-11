"use client";

import React, { useState } from "react";
import "./index.css"

/**
 * 全局底部栏组件
 * @constructor
 */
export default function GlobalFooter() {
  const currentYear = new Date().getFullYear();
  return (
    <div className="global-footer">
      <div>© {currentYear} Made with love</div>
      <div>by HJC</div>
    </div>
  );
}
