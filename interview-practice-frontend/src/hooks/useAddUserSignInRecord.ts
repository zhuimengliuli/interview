"use client";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import {useEffect, useState} from "react";
import {addUserSignInUsingPost,} from "@/api/userController";
import {message} from "antd";

/**
 * 用户签到记录钩子
 * @param props
 * @constructor
 */
const useAddUserSignInRecord = () => {
  const [loading, setLoading] = useState<boolean>(true);

  const doFetch = async () => {
    try {
      setLoading(false);
      await addUserSignInUsingPost();
      setLoading(true);
    } catch (e: any) {
      message.error("用户签到失败" + e.message);
    }
  };

  useEffect(() => {
    doFetch();
  }, []);

  return { loading };
};

export default useAddUserSignInRecord;
