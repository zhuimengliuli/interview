"use client";
import CreateModal from "./components/CreateModal";
import UpdateModal from "./components/UpdateModal";
import {
    batchDeleteQuestionUsingPost,
    deleteQuestionUsingPost,
    listQuestionByPageUsingPost,
} from "@/api/questionController";
import {PlusOutlined} from "@ant-design/icons";
import {ActionType, PageContainer, ProColumns, ProTable} from "@ant-design/pro-components";
import {Button, message, Popconfirm, Space, Table, Typography} from "antd";
import React, {useRef, useState} from "react";
import TagList from "@/components/TagList";
import MyEditor from "@/components/MyEditor";
import MdEditor from "@/components/MyEditor";
import UpdateBankModal from "@/app/admin/question/components/UpdateBankModal";
import BatchAddQuestionsToBankModal from "@/app/admin/question/components/BatchAddQuestionsToBankModal";
import BatchRemoveQuestionsFromBankModal from "@/app/admin/question/components/BatchRemoveQuestionsFromBankModal";

/**
 * 题库管理页面
 *
 * @constructor
 */
const QuestionAdminPage: React.FC = () => {
  // 是否显示新建窗口
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // 是否显示更新窗口
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  // 是否显示新建窗口
  const [
    batchAddQuestionsToBankModalVisible,
    setBatchAddQuestionsToBankModalVisible,
  ] = useState<boolean>(false);
  // 是否显示更新窗口
  const [
    batchRemoveQuestionsFromBankModalVisible,
    setBatchRemoveQuestionsFromBankModalVisible,
  ] = useState<boolean>(false);
  const [selectQuestionIdList, setSelectQuestionIdList] = useState<number[]>();
  // 是否显示更新所属题库窗口
  const [updateBankModalVisible, setUpdateBankModalVisible] =
    useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  // 当前题库点击的数据
  const [currentRow, setCurrentRow] = useState<API.Question>();

  /**
   * 删除节点
   *
   * @param row
   */
  const handleDelete = async (row: API.Question) => {
    const hide = message.loading("正在删除");
    if (!row) return true;
    try {
      await deleteQuestionUsingPost({
        id: row.id as any,
      });
      hide();
      message.success("删除成功");
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error("删除失败，" + error.message);
      return false;
    }
  };

    /**
     * 批量删除节点
     *
     * @param questionIdList
     */
    const batchHandleDelete = async (questionIdList: number[]) => {
        const hide = message.loading("正在删除");
        if (!questionIdList) return;
        try {
            await batchDeleteQuestionUsingPost({
                questionIdList
            });
            hide();
            message.success("删除成功");
            actionRef?.current?.reload();
        } catch (error: any) {
            hide();
            message.error("删除失败，" + error.message);
        }
    };

  /**
   * 表格列配置
   */
  const columns: ProColumns<API.Question>[] = [
    {
      title: "id",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "所属题库",
      dataIndex: "bankId",
      hideInTable: true,
      hideInForm: true,
    },
    {
      title: "标题",
      dataIndex: "title",
      valueType: "text",
    },
    {
      title: "内容",
      dataIndex: "content",
      valueType: "text",
      hideInSearch: true,
      width: 240,
      renderFormItem: (_, { fieldProps }, form) => {
        return <MyEditor {...fieldProps} />;
      },
    },
    {
      title: "答案",
      dataIndex: "answer",
      valueType: "text",
      hideInSearch: true,
      width: 640,
      renderFormItem: (_, { fieldProps }, form) => {
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "标签",
      dataIndex: "tags",
      valueType: "select",
      fieldProps: {
        mode: "tags",
      },
      render: (_, record) => {
        const tagList = JSON.parse(record.tags || "[]");
        return <TagList tagList={tagList} />;
      },
    },
    {
      title: "创建用户",
      dataIndex: "userId",
      valueType: "text",
      hideInForm: true,
    },

    {
      title: "创建时间",
      sorter: true,
      dataIndex: "createTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "编辑时间",
      sorter: true,
      dataIndex: "editTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "更新时间",
      sorter: true,
      dataIndex: "updateTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "操作",
      dataIndex: "option",
      valueType: "option",
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateBankModalVisible(true);
            }}
          >
            修改所属题库
          </Typography.Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            删除
          </Typography.Link>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.Question>
        headerTitle={"查询表格"}
        actionRef={actionRef}
        search={{
          labelWidth: 120,
        }}
        rowSelection={{
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
          defaultSelectedRowKeys: [1],
        }}
        tableAlertRender={({
          selectedRowKeys,
          selectedRows,
          onCleanSelected,
        }) => {
          console.log(selectedRowKeys, selectedRows);
          return (
            <Space size={24}>
              <span>
                已选 {selectedRowKeys.length} 项
                <a style={{ marginInlineStart: 8 }} onClick={onCleanSelected}>
                  取消选择
                </a>
              </span>
            </Space>
          );
        }}
        tableAlertOptionRender={({
          selectedRowKeys,
          selectedRows,
          onCleanSelected,
        }) => {
          return (
            <Space size={16}>
              <Button
                onClick={() => {
                  // 批量将题目加入题库
                  setSelectQuestionIdList(selectedRowKeys as number[]);
                  setBatchAddQuestionsToBankModalVisible(true);
                }}
              >
                批量将题目加入题库
              </Button>
              <Button
                onClick={() => {
                  // 批量将题目从题库删除
                  setSelectQuestionIdList(selectedRowKeys as number[]);
                  setBatchRemoveQuestionsFromBankModalVisible(true);
                }}
              >
                批量将题目从题库删除
              </Button>
              <Popconfirm
                title="删除题目"
                description="你确定要删除题目?"
                onConfirm={() => {
                  // 批量删除题目
                    batchHandleDelete(selectedRowKeys as number[]);
                }}
                okText="确定"
                cancelText="取消"
              >
                <Button danger>批量删除题目</Button>
              </Popconfirm>

              <a>导出数据</a>
            </Space>
          );
        }}
        rowKey="id"
        scroll={{ x: true }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCreateModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sort, filter) => {
          const sortField = Object.keys(sort)?.[0];
          const sortOrder = sort?.[sortField] ?? undefined;

          const { data, code } = await listQuestionByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionQueryRequest);
          return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
          };
        }}
        columns={columns}
      />
      <CreateModal
        visible={createModalVisible}
        columns={columns}
        onSubmit={() => {
          setCreateModalVisible(false);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setCreateModalVisible(false);
        }}
      />
      <UpdateModal
        visible={updateModalVisible}
        columns={columns}
        oldData={currentRow}
        onSubmit={() => {
          setUpdateModalVisible(false);
          setCurrentRow(undefined);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setUpdateModalVisible(false);
        }}
      />
      <UpdateBankModal
        visible={updateBankModalVisible}
        questionId={currentRow?.id}
        onCancel={() => {
          setUpdateBankModalVisible(false);
        }}
      />
      <BatchAddQuestionsToBankModal
        visible={batchAddQuestionsToBankModalVisible}
        onSubmit={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
        questionIdList={selectQuestionIdList}
      />
      <BatchRemoveQuestionsFromBankModal
        visible={batchRemoveQuestionsFromBankModalVisible}
        onSubmit={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
        questionIdList={selectQuestionIdList}
      />
    </PageContainer>
  );
};
export default QuestionAdminPage;
