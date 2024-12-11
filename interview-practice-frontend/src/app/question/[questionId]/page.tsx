"use server";
import "./index.css";
import {Flex, Menu} from "antd";
import { getBankVoByIdUsingGet } from "@/api/bankController";
import Title from "antd/es/typography/Title";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import Sider from "antd/es/layout/Sider";
import { Content } from "antd/es/layout/layout";
import QuestionCard from "@/components/QuestionCard";
import Link from "next/link";

/**
 * 题目详情页面
 * @constructor
 */
export default async function QuestionPage({ params }) {
  const { questionId } = params;

  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = res.data;
  } catch (e: any) {
    console.error("获取题目详情失败，" + e.message);
  }

  if (!question) {
    return <div>获取题目详情失败，请重试</div>;
  }

  return (
    <div id="questionPage">
      <QuestionCard question={question}/>
    </div>
  );
}
