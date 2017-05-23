package com.redaril.dmptf.util.network.appinterface.webservice;

import com.redaril.dmp.model.meta.*;
import com.redaril.dmp.service.ServiceException;
import com.redaril.dmptf.util.date.DateWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.fail;

public class WSHelper {
    private WSWrapper wsWrapper;
    private static Logger LOG;
    private final static int loopCount = 3;
    private final static String partnerName = "Core Audience";
    private final static String pacingOptionName = "No Restrictions";

    public WSHelper(String ENV) {
        LOG = LoggerFactory.getLogger(WSHelper.class);
        wsWrapper = new WSWrapper(ENV);
    }

    public PlatformClient createAdvertiser() {
        PlatformClient platformClient = new PlatformClient();
        // platformClient.setAdvertiser(true);
        platformClient.setBillingAddress("Hudson");
        platformClient.setContactEmail("auto@test.com");
        platformClient.setContactName("Auto" + DateWrapper.getRandom());
        platformClient.setName("Auto" + DateWrapper.getRandom());
        platformClient.setCreated(new Date());
        platformClient.setActive(true);
        platformClient.setDfpNone("rasegs=NoValue");
        platformClient.setAudiencescape(false);
        platformClient.setTagAnalytics(false);
        boolean isExec = false;
        int i = 0;
        PlatformClient newClient = null;
        while (!isExec & i < loopCount) {
            try {
                newClient = wsWrapper.getUserManagementService().createPlatformClient(platformClient);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create platformClient. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newClient == null) {
            fail("Can't create platformClient.");
        } else
            LOG.info("PlatformClient created successfully.Id = " + newClient.getId() + "Name = " + newClient.getName());
        return newClient;
    }

    public PlatformClient createPublisher() {
        PlatformClient platformClient = new PlatformClient();
        // platformClient.setAdvertiser(false);
        platformClient.setBillingAddress("Hudson");
        platformClient.setContactEmail("auto@test.com");
        platformClient.setContactName("Auto" + DateWrapper.getRandom());
        platformClient.setName("Auto" + DateWrapper.getRandom());
        platformClient.setCreated(new Date());
        platformClient.setActive(true);
        platformClient.setDfpNone("rasegs=NoValue");
        platformClient.setAudiencescape(false);
        platformClient.setTagAnalytics(false);
        boolean isExec = false;
        int i = 0;
        PlatformClient newClient = null;
        while (!isExec & i < loopCount) {
            try {
                newClient = wsWrapper.getUserManagementService().createPlatformClient(platformClient);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create platformClient. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newClient == null) {
            fail("Can't create platformClient.");
        } else
            LOG.info("PlatformClient created successfully.Id = " + newClient.getId() + "Name = " + newClient.getName());
        return newClient;
    }

    public long createAdvertiser(PlatformClient client) {
        Advertiser advertiser = new Advertiser();
        advertiser.setName("Auto" + DateWrapper.getRandom());
        advertiser.setPlatformClient(client);
        advertiser.setVersion(new Date());
        boolean isExec = false;
        int i = 0;
        long adv = 0;
        while (!isExec & i < loopCount) {
            try {
                adv = wsWrapper.getCategoryService().addAdvertiser(advertiser);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create advertiser. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (adv == 0) {
            fail("Can't create advertiser.");
        } else LOG.info("Advertiser was created successfully. Id = " + adv);
        return adv;
    }

    //createDataConsumer creates DataOwner too
    public DataConsumer createDataConsumer(PlatformClient platformClient) {
        DataConsumer dataConsumer = new DataConsumer();
        dataConsumer.setCreated(new Date());
        dataConsumer.setName("Auto" + DateWrapper.getRandom());
        dataConsumer.setPlatformClient(platformClient);
        boolean isExec = false;
        int i = 0;
        DataConsumer newDC = null;
        while (!isExec & i < loopCount) {
            try {
                newDC = wsWrapper.getUserManagementService().createDataConsumer(dataConsumer);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataConsumer. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newDC == null) {
            fail("Can't create DataConsumer.");
        } else LOG.info("DataConsumer was created successfully. Id = " + newDC.getId() + "Name = " + newDC.getName());
        return newDC;
    }

    public AudienceGroup createAudienceGroup(DataOwner dataOwner) {
        DataOwner newDO = new DataOwner();
        newDO.setId(dataOwner.getId());
        AudienceGroup audienceGroup = new AudienceGroup();
        audienceGroup.setName("Auto" + DateWrapper.getRandom());
        audienceGroup.setDataOwner(newDO);
        audienceGroup.setVersion(new Date());
        audienceGroup.setDeleted(false);
        boolean isExec = false;
        int i = 0;
        AudienceGroup newAG = null;
        while (!isExec & i < loopCount) {
            try {
                newAG = wsWrapper.getCategoryService().addAudienceGroup(audienceGroup);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AudienceGroup. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newAG == null) {
            fail("Can't create AudienceGroup.");
        } else LOG.info("AudienceGroup was created successfully. Id = " + newAG.getId());
        return newAG;
    }

    public long createAdvertiserCampaign(PlatformClient platformClient, long advId) {
        AdvertiserCampaign advertiserCampaign = new AdvertiserCampaign();
        advertiserCampaign.setName("Auto" + DateWrapper.getRandom());
        advertiserCampaign.setVersion(new Date());
        advertiserCampaign.setPlatformClient(platformClient);
        Advertiser advertiser = new Advertiser();
        advertiser.setId(advId);
        advertiserCampaign.setAdvertiser(advertiser);
        boolean isExec = false;
        int i = 0;
        long campId = 0;
        while (!isExec & i < loopCount) {
            try {
                campId = wsWrapper.getCategoryService().addAdvertiserCampaign(advertiserCampaign);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AdvertiserCampaign. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (campId == 0) {
            fail("Can't create AdvertiserCampaign.");
        } else LOG.info("AdvertiserCampaign was created successfully. Id = " + campId);
        return campId;
    }

    public Category createCategory(@Nullable Category parent, DataType dataType, DataSource dataSource) {
        Category category = new Category();
        category.setBuyable(true);
        category.setDataSource(dataSource);
        category.setName("Auto" + DateWrapper.getRandom());
        category.setExternalId(DateWrapper.getRandom());
        category.setParent(parent);
        category.setVersion(new Date());
        category.setDataType(dataType);
        category.setInitialValue(100f);
        category.setDeleted(false);
        // category.setFloorPrice(new BigDecimal("2"));
        // category.setAudience(10);
        boolean isExec = false;
        int i = 0;
        Category newCategory = null;
        while (!isExec & i < loopCount) {
            try {
                newCategory = wsWrapper.getCategoryService().createNode(category);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create Category. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newCategory == null) {
            fail("Can't create Category.");
        } else LOG.info("Category was created successfully. Id = " + newCategory.getId());
        return newCategory;
    }

    public DataType getDataTypeByName(String name) {
        boolean isExec = false;
        int i = 0;
        List<DataType> dataTypeList = null;
        while (!isExec & i < loopCount) {
            try {
                dataTypeList = wsWrapper.getCategoryService().getDataTypes();
                isExec = true;
                for (DataType aDataType : dataTypeList) {
                    if (aDataType.getName().contains(name)) {
                        return aDataType;
                    }
                }
                LOG.error("Can't find DataTypes with Name like" + name);
            } catch (Exception e) {
                LOG.error("Can't get DataTypes. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (dataTypeList == null) {
            fail("Can't get DataTypes.");
        }
        fail("Can't find DataTypes with Name like" + name);
        return null;
    }

    public PricingMethod getPricingMethodByName(String name) {
        boolean isExec = false;
        int i = 0;
        List<PricingMethod> pricingMethodList = null;
        while (!isExec & i < loopCount) {
            try {
                pricingMethodList = wsWrapper.getCategoryService().getPricingMethods();
                isExec = true;
                for (PricingMethod aPricingMethod : pricingMethodList) {
                    if (aPricingMethod.getName().contains(name)) {
                        return aPricingMethod;
                    }
                }
                LOG.error("Can't find PricingMethods with Name like" + name);
            } catch (Exception e) {
                LOG.error("Can't get PricingMethods. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (pricingMethodList == null) {
            fail("Can't get PricingMethods.");
        }
        fail("Can't find PricingMethods with Name like" + name);
        return null;
    }

    public SourceType getSourceTypeByName(String name) {
        boolean isExec = false;
        int i = 0;
        List<SourceType> sourceTypeList = null;
        while (!isExec & i < loopCount) {
            try {
                sourceTypeList = wsWrapper.getCategoryService().getSourceTypes();
                isExec = true;
                for (SourceType aSourceType : sourceTypeList) {
                    if (aSourceType.getName().contains(name)) {
                        return aSourceType;
                    }
                }
                LOG.error("Can't find SourceTypes with Name like" + name);
            } catch (Exception e) {
                LOG.error("Can't get SourceTypes. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (sourceTypeList == null) {
            fail("Can't get SourceTypes.");
        }
        fail("Can't find SourceTypes with Name like" + name);
        return null;
    }

    public DataSource createDataSource(DataOwner dataOwner, PricingMethod pricingMethod, SourceType sourceType) {
        DataSource dataSource = new DataSource();
        dataSource.setName("Auto" + DateWrapper.getRandom());
        dataSource.setDataOwner(dataOwner);
        dataSource.setPricingMethod(pricingMethod);
        dataSource.setSourceType(sourceType);
        dataSource.setVersion(new Date());
        dataSource.setDeleted(false);
        boolean isExec = false;
        int i = 0;
        DataSource newDataSource = null;
        while (!isExec & i < loopCount) {
            try {
                newDataSource = wsWrapper.getCategoryService().addDataSource(dataSource);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataSource. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newDataSource == null) {
            fail("Can't create DataSource.");
        } else LOG.info("DataSource was created successfully. Id = " + newDataSource.getId());
        return newDataSource;
    }

    public DataPixel updateDataPixel(DataPixel dataPixel, IncludeMode includeMode, Set<SystemPiggyback> systemPiggybacks) {
        dataPixel.setSystemPiggybacksInclude(includeMode);
        dataPixel.setSystemPiggybacks(systemPiggybacks);
        boolean isExec = false;
        int i = 0;
        DataPixel pixel = null;
        while (!isExec & i < loopCount) {
            try {
                pixel = (DataPixel) wsWrapper.getPixelService().updatePixel(dataPixel);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (pixel == null) {
            fail("Can't update DataPixel.");
        } else LOG.info("DataPixel was update successfully. TagId = " + pixel.getTagId());
        return pixel;
    }

    public SystemPiggyback createSystemPiggyback(String url) {
        SystemPiggyback systemPiggyback = new SystemPiggyback();
        systemPiggyback.setConditionExpression("req.partnerId > 1");
        systemPiggyback.setDelay(0);
        systemPiggyback.setDelayUnit(DelayUnit.SECONDS);
        systemPiggyback.setEndDate(DateWrapper.getDate(1));
        systemPiggyback.setName(DateWrapper.getRandom());
        systemPiggyback.setPostfixExpression("");
        systemPiggyback.setStartDate(DateWrapper.getDate(-1));
        systemPiggyback.setStatus(PiggybackStatus.ACTIVE);
        systemPiggyback.setTag("<img width=\"1\" height=\"1\" border=\"0\" src=\"%s\"/>");
        systemPiggyback.setUnlimited(true);
        systemPiggyback.setUrl(url);
        systemPiggyback.setWeight(1000);
        systemPiggyback.setHttpsSupported(false);
        systemPiggyback.setAsyncTag(false);
        boolean isExec = false;
        int i = 0;
        SystemPiggyback newPb = null;
        while (!isExec & i < loopCount) {
            try {
                newPb = wsWrapper.getPixelService().createSystemPiggyback(systemPiggyback);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create SystemPiggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPb == null) {
            fail("Can't create SystemPiggyback.");
        } else LOG.info("SystemPiggyback was created successfully. Id = " + newPb.getId());
        return newPb;
    }

    public DmpPiggyback createClientPiggyback(long dataOwnerId, Set<Pixel> pixels, String url) {
        DmpPiggyback dmpPiggyback = new DmpPiggyback();
        dmpPiggyback.setConditionExpression("ri.getRequestParameter(\"pid\") > 1");
        dmpPiggyback.setDelay(0);
        dmpPiggyback.setDelayUnit(DelayUnit.SECONDS);
        dmpPiggyback.setEndDate(DateWrapper.getDate(1));
        dmpPiggyback.setName(DateWrapper.getRandom());
        dmpPiggyback.setPostfixExpression("");
        dmpPiggyback.setStartDate(DateWrapper.getDate(-1));
        dmpPiggyback.setStatus(PiggybackStatus.ACTIVE);
        dmpPiggyback.setTag("<img width=\"1\" height=\"1\" border=\"0\" src=\"%s\"/>");
        dmpPiggyback.setUnlimited(true);
        dmpPiggyback.setUrl(url);
        dmpPiggyback.setWeight(1000);
        dmpPiggyback.setPixels(pixels);
        dmpPiggyback.setOnChange(false);
        dmpPiggyback.setAsyncTag(false);
        dmpPiggyback.setHttpsSupported(true);
        dmpPiggyback.setExternal(false);
        boolean isExec = false;
        int i = 0;
        DmpPiggyback newPb = null;
        while (!isExec & i < loopCount) {
            try {
                newPb = wsWrapper.getPixelService().createDmpPiggyback(dmpPiggyback, dataOwnerId, "test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DmpPiggyback. Exception = " + e.getLocalizedMessage());

            }
            i++;
        }
        if (newPb == null) {
            fail("Can't create DmpPiggyback.");
        } else LOG.info("DmpPiggyback was created successfully. Id = " + newPb.getId());
        return newPb;
    }

    public SystemPiggyback updateSystemPiggyback(SystemPiggyback systemPiggyback) {
        boolean isExec = false;
        int i = 0;
        SystemPiggyback newPb = null;
        while (!isExec & i < loopCount) {
            try {
                newPb = wsWrapper.getPixelService().updateSystemPiggyback(systemPiggyback);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't update SystemPiggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPb == null) {
            fail("Can't update SystemPiggyback.");
        } else LOG.info("SystemPiggyback was updated successfully. Id = " + newPb.getId());
        return newPb;

    }

    public DmpPiggyback updateDmpPiggyback(DmpPiggyback dmpPiggyback) {
        boolean isExec = false;
        int i = 0;
        DmpPiggyback newPb = null;
        Set<DataTransferProperty> dataTransferPropertySet = dmpPiggyback.getDataTransferProperties();
        for (DataTransferProperty dtp : dataTransferPropertySet) {
            dtp.setId(null);
        }
        dmpPiggyback.setDataTransferProperties(dataTransferPropertySet);
        while (!isExec & i < loopCount) {
            try {
                newPb = wsWrapper.getPixelService().updateDmpPiggyback(dmpPiggyback, dmpPiggyback.getDataOwner().getId());
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't update DmpPiggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPb == null) {
            fail("Can't update DmpPiggyback.");
        } else LOG.info("DmpPiggyback was updated successfully. Id = " + newPb.getId());
        return newPb;

    }

    public void deleteSystemPiggyback(SystemPiggyback systemPiggyback) {
        boolean isExec = false;
        int i = 0;
        while (!isExec & i < loopCount) {
            try {
                LOG.info("Delete System Piggyback with ID = " + systemPiggyback.getId());
                wsWrapper.getPixelService().deleteSystemPiggyback(systemPiggyback.getId());
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't delete System Piggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
    }

    public void deleteDmpPiggyback(DmpPiggyback clientPiggyback) {
        boolean isExec = false;
        int i = 0;
        while (!isExec & i < loopCount) {
            try {
                LOG.info("Delete Client Piggyback with ID = " + clientPiggyback.getId());
                wsWrapper.getPixelService().deleteDmpPiggyback(clientPiggyback.getId(), clientPiggyback.getDataOwner().getId());
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't delete Client Piggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
    }

    public void deleteAudienceGroup(AudienceGroup audienceGroup) {
        boolean isExec = false;
        int i = 0;
        while (!isExec & i < loopCount) {
            try {
                LOG.info("Delete AudienceGroup with ID = " + audienceGroup.getId());
                wsWrapper.getCategoryService().deleteAudienceGroup(audienceGroup.getId());
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't delete AudienceGroup. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
    }

    public void deleteDataOwner(DataOwner dataOwner) {
        try {
            LOG.info("Delete DataOwner with ID = " + dataOwner.getId());
            wsWrapper.getUserManagementService().deleteDataOwner(dataOwner.getId());
        } catch (Exception e) {
            LOG.error("Can't delete DataOwner. Exception = " + e.getLocalizedMessage());
        }
    }

    public void deleteDataConsumer(DataConsumer dataConsumer) {
        try {
            LOG.info("Delete DataConsumer with ID = " + dataConsumer.getId());
            wsWrapper.getUserManagementService().deleteDataConsumer(dataConsumer.getId());
        } catch (Exception e) {

            LOG.error("Can't delete DataConsumer. Exception = " + e.getLocalizedMessage());
        }
    }

    public void deletePlatformClient(PlatformClient platformClient) {
        try {
            LOG.info("Delete PlatformClient with ID = " + platformClient.getId());
            wsWrapper.getUserManagementService().deletePlatformClient(platformClient.getId());
        } catch (Exception e) {
            LOG.error("Can't delete PlatformClient. Exception = " + e.getLocalizedMessage());
        }
    }

    public RegexQualifier createRegexQualifier(DataSource dataSource, Category category) {
        RegexQualifier regexQualifier = new RegexQualifier();
        regexQualifier.setParameter("ndl");
        regexQualifier.setProbabilityLowerBound(0);
        regexQualifier.setProbabilityUpperBound(100);
        regexQualifier.setUpdatable(null);
        regexQualifier.setDataSource(dataSource);
        String url = "url.com/" + DateWrapper.getRandom();
        regexQualifier.setBaseUrl("http://" + url);
        regexQualifier.setRegex(url + "*");
        regexQualifier.setCategoryName(category.getName());
        regexQualifier.setExternalId(category.getExternalId());
        boolean isExec = false;
        int i = 0;
        RegexQualifier newQualifier = null;
        while (!isExec & i < loopCount) {
            try {
                newQualifier = wsWrapper.getQualifierService().addRegexQualifier(regexQualifier);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create RegexQualifier. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newQualifier == null) {
            fail("Can't create RegexQualifier.");
        } else LOG.info("RegexQualifier was created successfully. Id = " + newQualifier.getId());
        return newQualifier;
    }

    public long createAudienceSegment(long dataConsumerId) {
        String name = "auto" + DateWrapper.getRandom();

        boolean isExec = false;
        int i = 0;
        long newSegmentId = 0;
        String dataOutParameter = "rasegs=123";
        while (!isExec & i < loopCount) {
            try {
                newSegmentId = wsWrapper.getCategoryService().createAudienceSegment(dataConsumerId, name, dataOutParameter, true);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AudienceSegment. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newSegmentId == 0) {
            fail("Can't create AudienceSegment.");
        } else LOG.info("AudienceSegment was created successfully. Id = " + newSegmentId);
        return newSegmentId;
    }

    public void publishAudienceSegment(long newSegmentId) {
        boolean isExec = false;
        int i = 0;
        while (!isExec & i < loopCount) {
            try {
                wsWrapper.getCategoryService().setAudienceSegmentStatus(newSegmentId, AudienceSegmentStatus.PUBLISHED);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AudienceSegment. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
    }

    public long createAudienceSegment(long dataConsumerId, String dataOutParameter) {
        String name = "auto" + DateWrapper.getRandom();

        boolean isExec = false;
        int i = 0;
        long newSegmentId = 0;
        while (!isExec & i < loopCount) {
            try {
                newSegmentId = wsWrapper.getCategoryService().createAudienceSegment(dataConsumerId, name, dataOutParameter, true);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AudienceSegment. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newSegmentId == 0) {
            fail("Can't create AudienceSegment.");
        } else LOG.info("AudienceSegment was created successfully. Id = " + newSegmentId);
        return newSegmentId;
    }

    public long createSection(long asId, Category category) {
        Section section = new Section();
        section.setName("auto" + DateWrapper.getRandom());
        //subSection for section
        Subsection subsection = new Subsection();
        subsection.setDisplayOrder(1);
        //add link subsection-category
        SubsectionCategoryMapping subsectionCategoryMapping = new SubsectionCategoryMapping();
        subsectionCategoryMapping.setDisplayOrder(1);
        subsectionCategoryMapping.setCategory(category);
        subsectionCategoryMapping.setSubsection(subsection);
        subsectionCategoryMapping.setWeight(100f);
        Set<SubsectionCategoryMapping> set = new HashSet<SubsectionCategoryMapping>();
        set.add(subsectionCategoryMapping);
        subsection.setMappings(set);
        subsection.setSection(section);
        Set<Subsection> setSS = new HashSet<Subsection>();
        setSS.add(subsection);
        section.setSubsections(setSS);
        AudienceSegment audienceSegment = new AudienceSegment();
        audienceSegment.setId(asId);
        section.setAudienceSegment(audienceSegment);
        boolean isExec = false;
        int i = 0;
        long sectionId = 0;
        while (!isExec & i < loopCount) {
            try {
                sectionId = wsWrapper.getCategoryService().addSection(asId, section).getId();
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create Section. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (sectionId == 0) {
            fail("Can't create Section.");
        } else LOG.info("Section was created successfully. Id = " + sectionId);
        return sectionId;
    }

    public AudienceSegment getAudienceSegmentById(long asId) {
        int i = 0;
        AudienceSegment as;
        while (i < loopCount) {
            try {
                as = wsWrapper.getCategoryService().getAudienceSegment(asId);
                return as;
            } catch (Exception e) {
                LOG.error("Can't get AudienceSegment. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get AudienceSegment.");
        return null;
    }

    public long createDataCampaign(long dataConsumerId, AudienceSegment segment, ImpressionPixel pixel, DataOwner dataOwner, long sectionId, Set<AudienceGroup> setAG) {
        DataCampaign dataCampaign = new DataCampaign();
        dataCampaign.setName("auto" + DateWrapper.getRandom());
        dataCampaign.setStatus(DataCampaignStatus.ACTIVE);
        dataCampaign.setAudienceGroups(setAG);
        dataCampaign.setAudienceSegment(segment);
        dataCampaign.setImpressionPixel(pixel);
        DataConsumer dataConsumer = new DataConsumer();
        dataConsumer.setId(dataConsumerId);
        dataCampaign.setDataConsumer(dataConsumer);
        Set<MediaPartner> setMP = new HashSet<MediaPartner>();
        setMP.add(getMediaPartnerByName(partnerName));
        dataCampaign.setMediaPartners(setMP);
        DataCampaignProperty dataCampaignProperty = new DataCampaignProperty();
        dataCampaignProperty.setBudget(new BigDecimal(100));
        dataCampaignProperty.setStartDate(DateWrapper.getDate(-1));
        dataCampaignProperty.setEndDate(DateWrapper.getDate(1));
        dataCampaignProperty.setPacingOption(getPacingOptionByName(pacingOptionName));
        Section section = new Section();
        section.setId(sectionId);
        dataCampaignProperty.setSection(section);
        List<DataCampaignProperty> listDCP = new ArrayList<DataCampaignProperty>();
        listDCP.add(dataCampaignProperty);
        dataCampaign.setDataCampaignProperties(listDCP);
        boolean isExec = false;
        int i = 0;
        long newDC = 0;
        while (!isExec & i < loopCount) {
            try {
                newDC = wsWrapper.getCategoryService().createDataCampaign(dataCampaign, dataConsumerId, "auto");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataCampaign. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newDC == 0) {
            fail("Can't create DataCampaign.");
        } else LOG.info("DataCampaign was created successfully. Id = " + newDC);
        return newDC;
    }

    public MediaPartner getMediaPartnerByName(String name) {
        boolean isExec = false;
        int i = 0;
        List<MediaPartner> mediaPartnerList = null;
        while (!isExec & i < loopCount) {
            try {
                mediaPartnerList = wsWrapper.getCategoryService().getMediaPartners();
                isExec = true;
                for (MediaPartner mediaPartner : mediaPartnerList) {
                    if (mediaPartner.getName().contains(name)) {
                        return mediaPartner;
                    }
                }
                LOG.error("Can't find mediaPartner with Name like" + name);
            } catch (Exception e) {
                LOG.error("Can't get mediaPartner. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (mediaPartnerList == null) {
            fail("Can't get mediaPartner.");
        }
        fail("Can't find mediaPartner with Name like" + name);
        return null;
    }

    public PacingOption getPacingOptionByName(String name) {
        boolean isExec = false;
        int i = 0;
        List<PacingOption> pacingOptionList = null;
        while (!isExec & i < loopCount) {
            try {
                pacingOptionList = wsWrapper.getCategoryService().getPacingOptions();
                isExec = true;
                for (PacingOption pacingOption : pacingOptionList) {
                    if (pacingOption.getName().contains(name)) {
                        return pacingOption;
                    }
                }
                LOG.error("Can't find PacingOption with Name like" + name);
            } catch (Exception e) {
                LOG.error("Can't get PacingOption. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (pacingOptionList == null) {
            fail("Can't get PacingOption.");
        }
        fail("Can't find PacingOption with Name like" + name);
        return null;
    }

    /////////////////PIXELS//////////////////

    public AdvImpressionPixel createImpressionPixel(DataOwner dataOwner, Set<AudienceGroup> setAudienceGroup) {
        AdvImpressionPixel advImpressionPixel = new AdvImpressionPixel();
        advImpressionPixel.setVersion(new Date());
        advImpressionPixel.setDataOwner(dataOwner);
        advImpressionPixel.setActive(true);
        advImpressionPixel.setCreatedBy("Auto-test");
        advImpressionPixel.setName("Auto" + DateWrapper.getRandom());
        advImpressionPixel.setVersion(new Date());
        advImpressionPixel.setNotes("Auto" + DateWrapper.getRandom());
        advImpressionPixel.setAudienceGroups(setAudienceGroup);
        boolean isExec = false;
        int i = 0;
        AdvImpressionPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (AdvImpressionPixel) wsWrapper.getPixelService().createPixel(advImpressionPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create ImpressionPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create ImpressionPixel.");
        } else LOG.info("ImpressionPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public AdvClickPixel createClickPixel(DataOwner dataOwner, Set<AudienceGroup> setAudienceGroup, long mediaPartnerId) {
        AdvClickPixel advClickPixel = new AdvClickPixel();
        advClickPixel.setName("Auto" + DateWrapper.getRandom());
        advClickPixel.setNotes("Auto" + DateWrapper.getRandom());
        advClickPixel.setAudienceGroups(setAudienceGroup);
        advClickPixel.setActive(true);
        advClickPixel.setVersion(new Date());
        advClickPixel.setDataOwner(dataOwner);
        MediaPartner mediaPartner = new MediaPartner();
        mediaPartner.setId(mediaPartnerId);
        advClickPixel.setMediaPartner(mediaPartner);
        boolean isExec = false;
        int i = 0;
        AdvClickPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (AdvClickPixel) wsWrapper.getPixelService().createPixel(advClickPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AdvClickPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create AdvClickPixel.");
        } else LOG.info("AdvClickPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public AdvConversionPixel createConversionPixel(DataOwner dataOwner, Set<AudienceGroup> setAudienceGroup) {
        AdvConversionPixel advConversionPixel = new AdvConversionPixel();
        advConversionPixel.setDataOwner(dataOwner);
        advConversionPixel.setActive(true);
        advConversionPixel.setCreatedBy("Auto-test");
        advConversionPixel.setName("Auto" + DateWrapper.getRandom());
        advConversionPixel.setVersion(new Date());
        advConversionPixel.setNotes("Auto" + DateWrapper.getRandom());
        advConversionPixel.setAudienceGroups(setAudienceGroup);
        boolean isExec = false;
        int i = 0;
        AdvConversionPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (AdvConversionPixel) wsWrapper.getPixelService().createPixel(advConversionPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create AdvConversionPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create AdvConversionPixel.");
        } else LOG.info("AdvConversionPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public TrackingPixel createTrackingPixel(DataOwner dataOwner, Set<AudienceGroup> setAudienceGroup, String prodKey, String prodCatKey) {
        TrackingPixel trackingPixel = new TrackingPixel();
        trackingPixel.setName("Auto" + DateWrapper.getRandom());
        trackingPixel.setNotes("Auto" + DateWrapper.getRandom());
        trackingPixel.setAudienceGroups(setAudienceGroup);
        trackingPixel.setActive(true);
        trackingPixel.setVersion(new Date());
        trackingPixel.setDataOwner(dataOwner);
        trackingPixel.setProductKey(prodKey);
        trackingPixel.setProductCategoryKey(prodCatKey);
        boolean isExec = false;
        int i = 0;
        TrackingPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (TrackingPixel) wsWrapper.getPixelService().createPixel(trackingPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create TrackingPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create TrackingPixel.");
        } else LOG.info("TrackingPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public String createDataPixel(String dataSourceId) {
        DataOwner dataOwner = null;
        List<AudienceGroup> listGroups = null;
        try {
            List<DataSource> dataSources = wsWrapper.getCategoryService().getDataSources();
            for (DataSource d : dataSources) {
                if (d.getId() == Long.parseLong(dataSourceId)) {
                    dataOwner = d.getDataOwner();
                    break;
                }
            }
            if (dataOwner == null) {
                LOG.error("DATAOWNER NOT FOUND!");
                fail("DATAOWNER for dataSource NOT FOUND! dataSourceId = " + dataSourceId);
            } else {
                listGroups = wsWrapper.getCategoryService().getAudienceGroups(dataOwner.getId());
            }
            if (listGroups == null) {
                LOG.error("AUDIENCEGROUPS NOT FOUND!");
                fail("AUDIENCEGROUPS for dataOwner NOT FOUND! dataOwnerId = " + dataOwner.getId());
            }
            Set<AudienceGroup> stGroups = new HashSet<AudienceGroup>(listGroups);
            return createDataPixel(dataOwner, stGroups).getTagId().toString();
        } catch (ServiceException e) {
            LOG.error("Can't create pixel. Exception = " + e.getLocalizedMessage());
            return "";
        }
    }

    public DataPixel createDataPixel(DataOwner dataOwner, Set<AudienceGroup> setAudienceGroup) {
        DataPixel dataPixel = new DataPixel();
        dataPixel.setName("Auto" + DateWrapper.getRandom());
        dataPixel.setNotes("Auto" + DateWrapper.getRandom());
        dataPixel.setAudienceGroups(setAudienceGroup);
        dataPixel.setActive(true);
        dataPixel.setVersion(new Date());
        dataPixel.setSystemPiggybacksInclude(IncludeMode.ALL);
        dataPixel.setDataOwner(dataOwner);
        boolean isExec = false;
        int i = 0;
        DataPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (DataPixel) wsWrapper.getPixelService().createPixel(dataPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create DataPixel.");
        } else LOG.info("DataPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public void deleteDataPixel(DataPixel dataPixel) {
        boolean isExec = false;
        int i = 0;
        while (!isExec & i < loopCount) {
            try {
                LOG.info("Delete DataPixel with TagID = " + dataPixel.getTagId());
                wsWrapper.getPixelService().deletePixel(dataPixel.getId());
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't delete DataPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
    }

    public IntegrationPixel createAdServerIntegrationPixel(DataConsumer dataConsumer, Set<AudienceGroup> setAudienceGroup) {
        IntegrationPixel iPixel = new IntegrationPixel();
        iPixel.setName("Auto" + DateWrapper.getRandom());
        iPixel.setNotes("Auto" + DateWrapper.getRandom());
        iPixel.setAudienceGroups(setAudienceGroup);
        iPixel.setActive(true);
        iPixel.setVersion(new Date());
        iPixel.setDataConsumer(dataConsumer);
        boolean isExec = false;
        int i = 0;
        IntegrationPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (IntegrationPixel) wsWrapper.getPixelService().createPixel(iPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create IntegrationPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create IntegrationPixel.");
        } else LOG.info("IntegrationPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    ///////////DMP////////

    public ImpressionPixel createDMPImpressionPixel(DataConsumer dataConsumer, Set<AudienceGroup> setAudienceGroup, long advCampId) {
        ImpressionPixel iPixel = new ImpressionPixel();
        iPixel.setName("Auto" + DateWrapper.getRandom());
        iPixel.setNotes("Auto" + DateWrapper.getRandom());
        iPixel.setAudienceGroups(setAudienceGroup);
        iPixel.setActive(true);
        iPixel.setVersion(new Date());
        AdvertiserCampaign advCamp = new AdvertiserCampaign();
        advCamp.setId(advCampId);
        iPixel.setAdCampaign(advCamp);
        iPixel.setRedirect(null);
        iPixel.setDataConsumer(dataConsumer);
        boolean isExec = false;
        int i = 0;
        ImpressionPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (ImpressionPixel) wsWrapper.getPixelService().createPixel(iPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create ImpressionPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create ImpressionPixel.");
        } else LOG.info("ImpressionPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public ClickPixel createDMPClickPixel(DataConsumer dataConsumer, Set<AudienceGroup> setAudienceGroup, Set<ImpressionPixel> imprPixels){//}, long advCampId) {
        ClickPixel clickPixel = new ClickPixel();
        clickPixel.setDataConsumer(dataConsumer);
        clickPixel.setActive(true);
        clickPixel.setCreatedBy("Auto-test");
        clickPixel.setName("Auto" + DateWrapper.getRandom());
        clickPixel.setAudienceGroups(setAudienceGroup);
        clickPixel.setImpressionPixels(imprPixels);
        boolean isExec = false;
        int i = 0;
        ClickPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (ClickPixel) wsWrapper.getPixelService().createPixel(clickPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DMPClickPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create DMPClickPixel.");
        } else LOG.info("DMPClickPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }

    public ConversionPixel createDMPConversionPixel(DataConsumer dataConsumer, Set<AudienceGroup> setAudienceGroup,Set<ImpressionPixel> imprPixels){//, long advCampId) {
        ConversionPixel iPixel = new ConversionPixel();
        iPixel.setName("Auto" + DateWrapper.getRandom());
        iPixel.setNotes("Auto" + DateWrapper.getRandom());
        iPixel.setAudienceGroups(setAudienceGroup);
        iPixel.setActive(true);
        iPixel.setVersion(new Date());
        iPixel.setDataConsumer(dataConsumer);
        iPixel.setImpressionPixels(imprPixels);
        boolean isExec = false;
        int i = 0;
        ConversionPixel newPixel = null;
        while (!isExec & i < loopCount) {
            try {
                newPixel = (ConversionPixel) wsWrapper.getPixelService().createPixel(iPixel, "auto-test");
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create ConversionPixel. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newPixel == null) {
            fail("Can't create ConversionPixel.");
        } else LOG.info("ConversionPixel was created successfully. TagId = " + newPixel.getTagId());
        return newPixel;
    }


    /////////////////PIXELS//////////////////



    public DataSale updateDataSale(DataConsumer dataConsumer, PricingMethod pricingMethod, Category category) {
        DataSale dataSale = new DataSale();
        List<DataSaleConsumer> consumerList = new ArrayList<DataSaleConsumer>();
        DataSaleConsumer dataSaleConsumer = new DataSaleConsumer();
        dataSaleConsumer.setConsumerEntity(dataConsumer);
        dataSaleConsumer.setPricingMethod(pricingMethod);
        dataSaleConsumer.setApplication(getApplications().get(0));
        consumerList.add(dataSaleConsumer);
        dataSale.setName("Auto" + DateWrapper.getRandom());
        dataSale.setConsumers(consumerList);
        dataSale.setDataOwner(dataConsumer.getDataOwner());
        List<DataOwnerCategory> dataOwnerCategoryList = new ArrayList<DataOwnerCategory>();
        DataOwnerCategory dataOwnerCategory = new DataOwnerCategory();
        dataOwnerCategory.setDataCategory(category);
        dataOwnerCategory.setPrice(new BigDecimal(10));
        dataOwnerCategory.setInherited(false);
        dataOwnerCategoryList.add(dataOwnerCategory);
        dataSale.setCategories(dataOwnerCategoryList);
        dataSale.setExpired(new Date(DateWrapper.getLongTime() + 100000));
        boolean isExec = false;
        int i = 0;
        DataSale newDataSale = null;
        while (!isExec & i < loopCount) {
            try {
                newDataSale = wsWrapper.getCategoryService().updateDataSale(dataSale);
                isExec = true;
            } catch (Exception e) {
                LOG.error("Can't create DataSale. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        if (newDataSale == null) {
            fail("Can't create DataSale.");
        } else LOG.info("DataSale was created successfully. Id = " + newDataSale.getId());
        return newDataSale;
    }

    public List<Application> getApplications() {
        int i = 0;
        List<Application> appList = null;
        while (i < loopCount) {
            try {
                appList = wsWrapper.getCategoryService().getApplications();
                return appList;
            } catch (Exception e) {
                LOG.error("Can't find Application.");
            }
            i++;
        }
        fail("Can't get Application.");
        return appList;
    }

    public List<SystemPiggyback> getSystemPiggybacks() {
        int i = 0;
        List<SystemPiggyback> list = null;
        while (i < loopCount) {
            try {
                list = wsWrapper.getCoreService().getSystemPiggybacks();
                return list;
            } catch (Exception e) {
                LOG.error("Can't get SystemPiggybacks. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get SystemPiggybacks.");
        return list;
    }

    public SystemPiggyback getSystemPiggyback(long id) {
        int i = 0;
        SystemPiggyback pb = null;
        while (i < loopCount) {
            try {
                pb = wsWrapper.getPixelService().getSystemPiggyback(id);
                return pb;
            } catch (Exception e) {
                LOG.error("Can't get SystemPiggyback. Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get SystemPiggyback.");
        return pb;
    }

    public List<DmpPiggyback> getDmpPiggybacks(long dataOwnerId) {
        int i = 0;
        List<DmpPiggyback> list = null;
        while (i < loopCount) {
            try {
                list = wsWrapper.getPixelService().getDmpPiggybacks(dataOwnerId);
                return list;
            } catch (Exception e) {
                LOG.error("Can't get DmpPiggybacks by dataOwnerId = " + dataOwnerId + ". Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get DmpPiggybacks by dataOwnerId = " + dataOwnerId);
        return list;
    }

    public DataOwner getDataOwnerById(long id) {
        int i = 0;
        DataOwner list = new DataOwner();
        while (i < loopCount) {
            try {
                list = wsWrapper.getUserManagementService().getDataOwner(id);
                return list;
            } catch (Exception e) {
                LOG.error("Can't get DataOwner by id = " + id + ". Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get DataOwner by id = " + id);
        return list;
    }

    public List<Category> getCategories(long id) {
        int i = 0;
        List<Category> list = new ArrayList<>();
        while (i < loopCount) {
            try {
                list = wsWrapper.getCategoryService().getCategories(id);
                return list;
            } catch (Exception e) {
                LOG.error("Can't get Categories by dataSourceId = " + id + ". Exception = " + e.getLocalizedMessage());
            }
            i++;
        }
        fail("Can't get Categories by dataSourceId = " + id);
        return list;
    }

    public Category getActiveCategory(long id) {
        List<Category> categoryList = getCategories(id);
        for (Category category : categoryList) {
            if (category.getBuyable() == true && category.getDeleted() == false) return category;
        }
        return null;
    }

//    public DataSaleConsumer createDataSaleConsumer(DataOwner dataOwner, PricingMethod pricingMethod, DataConsumer dataConsumer){
//        DataSaleConsumer dataSaleConsumer = new DataSaleConsumer();
//        dataSaleConsumer.setConsumerEntity(dataConsumer);
//        boolean isExec = false;
//        int i = 0;
//        DataSale newDataSale = null;
//        while (!isExec & i < loopCount) {
//            try {
//                newDataSale = wsWrapper.getCategoryService().cre
//                isExec=true;
//            } catch (Exception e) {
//                LOG.error("Can't create DataSale. Exception = " + e.getLocalizedMessage());
//            }
//            i++;
//        }
//        if (newDataSale==null){
//            fail("Can't create DataSale.");
//        }
//        else LOG.info("DataSale was created successfully. Id = " + newDataSale.getId());
//        return newDataSale;
//    }
}