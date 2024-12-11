"use server";
import "./index.css";
import Title from "antd/es/typography/Title";
import {message} from "antd";
import {listBankVoByPageUsingPost} from "@/api/bankController";
import BankList from "@/components/BankList";

/**
 * 题库列表页面
 * @constructor
 */
export default async function BanksPage() {
  let bankList = [];
  // 题库数量（总数不多直接设置200）
  const pageSize = 200;
  try {
    const res = await listBankVoByPageUsingPost({
      pageSize,
      sortField: "createTime",
      sortOrder: "descend",
    });
    bankList = res.data.records ?? [];
  } catch (e: any) {
    message.error("获取题库列表失败，" + e.message);
  }

  return (
    <div id="banksPage" className="max-width-content">
      <Title level={3}>题库大全</Title>
      <BankList bankList={bankList} />
    </div>
  );
}
