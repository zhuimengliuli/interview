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
 * 题库题目详情页面
 * @constructor
 */
export default async function BankQuestionPage({ params }) {
  const { bankId, questionId } = params;
  let bank = undefined;
  try {
    const res = await getBankVoByIdUsingGet({
      id: bankId,
      needQueryQuestionList: true,
      pageSize: 200,
    });
    bank = res.data;
  } catch (e: any) {
    console.error("获取题库详情失败，" + e.message);
  }

  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = res.data;
  } catch (e: any) {
    console.error("获取题目详情失败，" + e.message);
  }
  if (!bank) {
    return <div>获取题库详情失败，请重试</div>;
  }
  if (!question) {
    return <div>获取题目详情失败，请重试</div>;
  }
  // 题目菜单列表
  const questionMenuItemList = (bank.page?.records || []).map((q) => {
    return {
      label: <Link href={`/bank/${bank.id}/question/${q.id}`}>{q.title}</Link>,
      key: q.id,
    };
  });
  return (
    <div id="bankQuestionPage">
      <Flex gap={24}>
        <Sider width={240} theme="light" style={{ padding: "24px 0" }}>
          <Title level={4} style={{ padding: "0 20px" }}>
            {bank.title}
          </Title>
          <Menu items={questionMenuItemList} selectedKeys={[question.id]}/>
        </Sider>
        <Content>
          <QuestionCard question={question}/>
        </Content>
      </Flex>
    </div>
  );
}
