"use server";
import "./index.css";
import { Avatar, Button, Card } from "antd";
import { getBankVoByIdUsingGet } from "@/api/bankController";
import Paragraph from "antd/es/typography/Paragraph";
import Meta from "antd/es/card/Meta";
import Title from "antd/es/typography/Title";
import QuestionList from "@/components/QuestionList";

/**
 * 题库详情页面
 * @constructor
 */
export default async function BankPage({ params }) {
  const { bankId } = params;
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

  if (!bank) {
    return <div>获取题库详情失败，请重试</div>;
  }

  let firstQuestionId;
  if (bank.page?.records && bank.page.records.length > 0) {
    firstQuestionId = bank.page.records[0].id;
  }
  return (
    <div id="bankPage" className="max-width-content">
      <Card>
        <Meta
          avatar={<Avatar src={bank.picture} size={72} />}
          title={
            <Title level={3} style={{ marginBottom: 0 }}>
              {bank.title}
            </Title>
          }
          description={
            <>
              <Paragraph type="secondary">{bank.description}</Paragraph>
              <Button
                type="primary"
                shape="round"
                href={`/bank/${bank.id}/question/${firstQuestionId}`}
                target="_blank"
                disabled={!firstQuestionId}
              >
                开始刷题
              </Button>
            </>
          }
        />
      </Card>
      <div style={{ marginBottom: 16 }} />
      <QuestionList
        questionList={bank.page?.records ?? []}
        cardTitle={`题目列表(${bank.page.total ?? 0})`}
        bankId={bank.id}
      />
    </div>
  );
}
